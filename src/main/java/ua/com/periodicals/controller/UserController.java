package ua.com.periodicals.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.model.Cart;
import ua.com.periodicals.security.MyUserDetailsService;
import ua.com.periodicals.security.UserPrincipal;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import java.util.List;
import java.util.Optional;

@SessionAttributes("cart")
@Controller
public class UserController {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    Environment env;

    @GetMapping("/")
    public String getWelcome(Model model) {

        return "user/main";
    }

    @GetMapping("/main")
    public ModelAndView getPeriodicalsPerPage(@RequestParam("page") Optional<String> page) {
        LOG.debug("Try to show list-periodicals view, page={}", page.orElse(null));

        int currentPage = page.isPresent() ? Integer.parseInt(page.get()) : 1;
        int itemsPerPage = Integer.parseInt(env.getProperty("admin_periodicals_per_page"));
        List<Periodical> periodicals = periodicalService.getPeriodicalsPage(currentPage, itemsPerPage);
        int totalPages = (int) Math.ceil((periodicalService.getCount() / itemsPerPage));

        ModelAndView mav = new ModelAndView("user/main");

        mav.addObject("periodicals", periodicals);
        mav.addObject("currentPage", currentPage);
        mav.addObject("totalPages", totalPages);

        return mav;
    }

    @PostMapping("/main")
    public ModelAndView addToCart(@RequestParam(value = "id") String id,
                                  @RequestParam("currentPage") String currentPage,
                                  @RequestParam("totalPages") String totalPages,
                                  Model model) {
        LOG.debug("Try to add periodical to cart, id={}", id);
        LOG.info("Request params... currentPage: {}, totalPages: {}", currentPage, totalPages);

        ModelAndView mav = new ModelAndView();
        Periodical periodical = periodicalService.getById(Long.parseLong(id));
        User loggedUser = myUserDetailsService.getLoggedUser();

        if (userService.isUserSubscribedToPeriodical(loggedUser.getId(), periodical.getId())
            || userService.isPeriodicalInUnpaidInvoice(loggedUser.getId(), periodical.getId())
        ) {
            mav.setViewName("user/main");
            mav.addObject("alreadySubscribed", "Already Subscribed");

            int itemsPerPage = Integer.parseInt(env.getProperty("admin_periodicals_per_page"));
            List<Periodical> periodicals = periodicalService.getPeriodicalsPage(Integer.parseInt(currentPage), itemsPerPage);
            mav.addObject("currentPage", Integer.parseInt(currentPage));
            mav.addObject("totalPages", Integer.parseInt(totalPages));
            mav.addObject("periodicals", periodicals);

        } else {

            Cart cart = (Cart) model.getAttribute("cart");
            LOG.debug("Got cart from session scope: {}", cart);

            if (cart == null) {
                cart = new Cart();
            }

            cart.addItem(periodical);
            model.addAttribute("cart", cart);

            mav.setViewName("redirect:/main/cart");
        }

        return mav;
    }

    @PostMapping("/main/cart/remove")
    public String removeFromCart(@RequestParam(value = "periodicalId") String id, @ModelAttribute Cart cart, WebRequest request, SessionStatus status) {
        LOG.debug("Try to remove from cart, periodicalId={}", id);
        cart.removeItem(Long.parseLong(id));

        if (cart.getTotalCount() == 0) {
            status.setComplete();
            request.removeAttribute("cart", WebRequest.SCOPE_SESSION);
        }

        return "redirect:/main/cart";
    }

    @GetMapping("/main/cart")
    public String showCart(Model model) {
        LOG.info("Try to show cart view");
        Cart cart = (Cart) model.getAttribute("cart");

        model.addAttribute("cart", cart);
        return "user/cart";
    }

    @PostMapping("/my/cart/submit")
    public String submitInvoice(@ModelAttribute Cart cart, WebRequest request, SessionStatus status) {
        LOG.info("Try to submit invoice");

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Logged in user id={}", principal.getId());

        try {
            invoiceService.submitInvoice(principal.getId(), cart);
        } catch (Exception e) {
            LOG.error("Failed to get submit cart: ", e);
            return "user/cart";
        }

        status.setComplete();
        request.removeAttribute("cart", WebRequest.SCOPE_SESSION);

        return "redirect:/main/cart";
    }

    @GetMapping("/main/subscriptions")
    public ModelAndView showActiveSubscriptions() {
        LOG.debug("Try to show active subscriptions");

        ModelAndView modelAndView = new ModelAndView("user/subscriptions");
        modelAndView.addObject("subscriptions", myUserDetailsService.getLoggedUser().getSubscriptions());

        return modelAndView;
    }

    @PostMapping("/main/subscriptions")
    public String unsubscribe(@RequestParam("id") String periodicalId) {
        LOG.debug("Try to remove from subscriptions, periodicalId={}", periodicalId);
        long userId = myUserDetailsService.getLoggedUser().getId();

        userService.unsubscribe(Long.parseLong(periodicalId), userId);
        return "redirect:/main/subscriptions";
    }

}

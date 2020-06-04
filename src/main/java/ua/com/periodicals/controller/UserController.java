package ua.com.periodicals.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import ua.com.periodicals.security.UserPrincipal;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionAttributes("cart")
@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    InvoiceService invoiceService;

    @RequestMapping("/")
    public String getWelcome(Model model) {

        return "user/main";
    }

    @RequestMapping("/main")
    public ModelAndView getAllPeriodicals(Model model) {
        LOG.debug("Try to show list-periodicals view");

        ModelAndView mav = new ModelAndView("user/main");
        List<Periodical> periodicals = periodicalService.getAllPeriodicals();

        mav.addObject("periodicals", periodicals);
        return mav;
    }

    @PostMapping("/main")
    public ModelAndView addToCart(@RequestParam(value = "id") String id, Model model) {
        LOG.debug("Try to add periodical to cart, id={}", id);

        ModelAndView mav = new ModelAndView();

        Periodical periodical = periodicalService.getById(Long.parseLong(id));
        LOG.info("Periodical: {}", periodical);

        User loggedUser = userService.getLoggedUser();
        LOG.info("Logged user: {}", loggedUser);

        if (userService.isUserSubscribedToPeriodical(loggedUser.getId(), periodical.getId())) {
            mav.setViewName("user/main");
            mav.addObject("alreadySubscribed", "Already Subscribed");

            List<Periodical> periodicals = periodicalService.getAllPeriodicals();
            mav.addObject("periodicals", periodicals);

            return mav;
        } else {
            Cart cart = (Cart) model.getAttribute("cart");
            LOG.debug("Got cart from session scope: {}", cart);

            if (cart == null) {
                cart = new Cart();
            }

            cart.addItem(periodical);
            model.addAttribute("cart", cart);

            mav.setViewName("redirect:/main/cart");
            return mav;
        }
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

        Set<Periodical> subscriptions = userService.getActiveSubscriptions();
        LOG.info("Active subscriptions: {}", subscriptions);

        modelAndView.addObject("subscriptions", subscriptions);

        return modelAndView;
    }


    @PostMapping("/main/subscriptions")
    public String unsubscribe(@RequestParam("id") String periodicalId) {
        LOG.debug("Try to remove from subscriptions, periodicalId={}", periodicalId);

        User user = userService.getLoggedUser();
        userService.unsubscribe(user.getId(), Long.parseLong(periodicalId));
//        ModelAndView mav = new ModelAndView();
        return "redirect:/main/subscriptions";
    }

}

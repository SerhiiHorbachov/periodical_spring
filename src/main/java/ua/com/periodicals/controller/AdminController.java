package ua.com.periodicals.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.periodicals.dto.PeriodicalDto;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.model.Cart;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    Environment env;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserService userService;

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin/periodicals")
    public ModelAndView getPeriodicalsPerPage(@RequestParam("page") Optional<String> page) {
        LOG.debug("Try to show list-periodicals view, page={}", page.orElse(null));

        int currentPage = page.isPresent() ? Integer.parseInt(page.get()) : 1;
        int itemsPerPage = Integer.parseInt(env.getProperty("admin_periodicals_per_page"));
        List<Periodical> periodicals = periodicalService.getPeriodicalsPage(currentPage, itemsPerPage);
        int totalPages = (int) Math.ceil((periodicalService.getCount() / itemsPerPage));

        if (Double.valueOf(periodicalService.getCount()) % itemsPerPage != 0) {
            totalPages = totalPages + 1;
        }

        ModelAndView mav = new ModelAndView("admin/periodicals/list-periodicals");
        mav.addObject("periodicals", periodicals);
        mav.addObject("currentPage", currentPage);
        mav.addObject("totalPages", totalPages);

        return mav;
    }

    @GetMapping("/admin/periodicals/new")
    public ModelAndView showNewPeriodicalForm() {
        LOG.debug("Try to show add periodical form");

        PeriodicalDto periodicalDto = new PeriodicalDto();

        ModelAndView mav = new ModelAndView("admin/periodicals/new-periodical");
        mav.addObject("periodical", periodicalDto);

        return mav;
    }


    @PostMapping("/admin/periodicals/new")
    public ModelAndView savePeriodical(@ModelAttribute("periodical") @Valid PeriodicalDto periodical,
                                       BindingResult bindingResult) {

        LOG.debug("Try to save new periodical: {}", periodical);
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/periodicals/new-periodical");
            return modelAndView;
        }

        Periodical periodicalToSave = new Periodical(
            periodical.getName(),
            periodical.getDescription(),
            (long) periodical.getMonthlyPrice() * 100);

        periodicalService.save(periodicalToSave);

        return new ModelAndView("redirect:/admin/periodicals");

    }

    @GetMapping("admin/invoices")
    public ModelAndView listInvoices() {
        LOG.debug("Try to get in progress invoices");

        ModelAndView modelAndView = new ModelAndView("admin/invoice/list-invoices");
        List<Invoice> invoices = invoiceService.getUnprocessedInvoices();
        modelAndView.addObject("invoices", invoices);

        return modelAndView;
    }


    @GetMapping("/admin/invoices/view")
    public ModelAndView viewInvoice(@RequestParam(value = "id") String id) {
        LOG.info("Try to show invoice id={}", id);

        ModelAndView modelAndView = new ModelAndView("admin/invoice/invoice");

        Invoice invoice = invoiceService.getById(Long.parseLong(id));

        List<Periodical> periodicals = periodicalService.findAllByInvoiceId(invoice.getId());

        Cart cart = new Cart();

        for (Periodical periodical : periodicals) {
            cart.addItem(periodical);
        }

        User user = userService.findById(invoice.getUserId());

        modelAndView.addObject("invoice", invoice);
        modelAndView.addObject("cart", cart);
        modelAndView.addObject("user", user);

        return modelAndView;
    }


    @PostMapping("/admin/invoices/view")
    public String processInvoice(@RequestParam(value = "command") String command,
                                 @RequestParam(value = "id") String invoiceId
    ) {
        LOG.info("Try to process invoice id={}, command={}", invoiceId, command);

        switch (command) {
            case "approve":
                LOG.info("Try to approve");
                invoiceService.approveInvoice(Long.parseLong(invoiceId));
                break;
            case "cancel":
                LOG.info("Try to cancel");
                invoiceService.cancelInvoice(Long.parseLong(invoiceId));
                break;

        }

        return "redirect:/admin/invoices/view?id=" + invoiceId;
    }

    @GetMapping("/admin/periodicals/edit")
    public ModelAndView updatePeriodicalView(@RequestParam("id") String id) {
        LOG.debug("Try to show edit periodicals form, periodical id={}", id);

        ModelAndView modelAndView = new ModelAndView("admin/periodicals/edit");

        Periodical periodical = periodicalService.getById(Long.parseLong(id));

        float price = Float.valueOf(periodical.getMonthlyPrice()) / 100;

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setId(periodical.getId());
        periodicalDto.setName(periodical.getName());
        periodicalDto.setDescription(periodical.getDescription());
        periodicalDto.setMonthlyPrice(price);

        LOG.debug("PeriodicalDto: {}", periodicalDto);

        modelAndView.addObject("periodical", periodicalDto);

        return modelAndView;

    }


    @PostMapping("/admin/periodicals/edit")
    public ModelAndView updatePeriodical(@ModelAttribute("periodical") @Valid PeriodicalDto periodical,
                                         BindingResult bindingResult) {
        LOG.debug("Try to update periodical, periodicalDto: {}", periodical);
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/periodicals/edit");
            return modelAndView;
        }

        Periodical periodicalToUpdate = periodicalService.getById(periodical.getId());

        periodicalToUpdate.setName(periodical.getName());
        periodicalToUpdate.setDescription(periodical.getDescription());

        long price = (long) periodical.getMonthlyPrice() * 100;
        LOG.debug("Price: {}", price);
        periodicalToUpdate.setMonthlyPrice(price);

        periodicalService.update(periodicalToUpdate);

        return new ModelAndView("redirect:/admin/periodicals");

    }

    @PostMapping("/admin/periodicals/delete")
    public ModelAndView deletePeriodical(@RequestParam("id") String id) {
        LOG.debug("Try to delete periodical by id={}", id);

        periodicalService.deleteById(Long.parseLong(id));

        return new ModelAndView("redirect:/admin/periodicals");

    }

}

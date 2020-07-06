package ua.com.periodicals.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import ua.com.periodicals.exception.EntityEngagedException;
import ua.com.periodicals.model.Cart;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(AdminController.class);

    private final static String PERIODICAL_ATTR = "periodical";
    private final static String PERIODICALS_ATTR = "periodicals";
    private final static String CURRENT_PAGE_ATTR = "currentPage";
    private final static String TOTAL_PAGES_ATTR = "totalPages";
    private final static String INVOICE_ATTR = "invoice";
    private final static String INVOICES_ATTR = "invoices";
    private final static String CART_ATTR = "cart";
    private final static String USER_ATTR = "user";
    private final static String PERIODICAL_IN_USE_ATTR = "periodicalInUse";

    private final static String APPROVE_CMD = "approve";
    private final static String CANCEL_CMD = "cancel";

    private final static String ADMIN_PERIODICALS_PAGE = "admin/periodicals/list-periodicals";
    private final static String ADMIN_NEW_PERIODICAL_PAGE = "admin/periodicals/new-periodical";
    private final static String ADMIN_LIST_INVOICES_PAGE = "admin/invoice/list-invoices";
    private final static String ADMIN_INVOICE_PAGE = "admin/invoice/invoice";
    private final static String ADMIN_PERIODICALS_EDIT_PAGE = "admin/periodicals/edit";

    private final static String ADMIN_PERIODICALS_PATH = "/admin/periodicals";
    private final static String ADMIN_INVOICES_VIEW_PATH = "/admin/invoices/view";

    @Autowired
    Environment env;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserService userService;


    /**
     * @param page
     * @return
     */
    @GetMapping("/admin/periodicals")
    public ModelAndView getPeriodicalsPerPage(@RequestParam("page") Optional<String> page) {
        LOG.debug("Try to show list-periodicals view, page={}", page.orElse(null));

        int currentPage = page.isPresent() ? Integer.parseInt(page.get()) : 1;
        int itemsPerPage = Integer.parseInt(env.getProperty("admin_periodicals_per_page"));
        int totalPages = (int) Math.ceil((periodicalService.getCount() / itemsPerPage));

        if (Double.valueOf(periodicalService.getCount()) % itemsPerPage != 0) {
            totalPages = totalPages + 1;
        }

        List<Periodical> periodicals = periodicalService.getPeriodicalsPage(currentPage, itemsPerPage);

        ModelAndView mav = new ModelAndView(ADMIN_PERIODICALS_PAGE);
        mav.addObject(PERIODICALS_ATTR, periodicals);
        mav.addObject(CURRENT_PAGE_ATTR, currentPage);
        mav.addObject(TOTAL_PAGES_ATTR, totalPages);

        return mav;
    }

    /**
     * @return
     */
    @GetMapping("/admin/periodicals/new")
    public ModelAndView showNewPeriodicalForm() {
        LOG.debug("Try to show add periodical form");

        PeriodicalDto periodicalDto = new PeriodicalDto();

        ModelAndView mav = new ModelAndView(ADMIN_NEW_PERIODICAL_PAGE);
        mav.addObject(PERIODICAL_ATTR, periodicalDto);

        return mav;
    }

    /**
     * @param periodical
     * @param bindingResult
     * @return
     */
    @PostMapping("/admin/periodicals/new")
    public ModelAndView savePeriodical(@ModelAttribute(PERIODICAL_ATTR) @Valid PeriodicalDto periodical,
                                       BindingResult bindingResult) {
        LOG.debug("Try to save new periodical: {}", periodical);

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName(ADMIN_NEW_PERIODICAL_PAGE);
            return modelAndView;
        }

        Periodical periodicalToSave = new Periodical(
            periodical.getName(),
            periodical.getDescription(),
            (long) periodical.getMonthlyPrice() * 100);

        periodicalService.save(periodicalToSave);

        return new ModelAndView("redirect:" + ADMIN_PERIODICALS_PATH);

    }

    /**
     * @return
     */
    @GetMapping("/admin/invoices")
    public ModelAndView listUnprocessedInvoices() {
        LOG.debug("Try to get in progress invoices");

        ModelAndView modelAndView = new ModelAndView(ADMIN_LIST_INVOICES_PAGE);
        List<Invoice> invoices = invoiceService.getUnprocessedInvoices();
        modelAndView.addObject(INVOICES_ATTR, invoices);

        return modelAndView;
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/admin/invoices/view")
    public ModelAndView viewInvoice(@RequestParam(value = "id") String id) {
        LOG.debug("Try to show invoice id={}", id);

        ModelAndView modelAndView = new ModelAndView(ADMIN_INVOICE_PAGE);

        Invoice invoice = invoiceService.getById(Long.parseLong(id));

        List<Periodical> periodicals = periodicalService.findAllByInvoiceId(invoice.getId());

        Cart cart = new Cart();

        for (Periodical periodical : periodicals) {
            cart.addItem(periodical);
        }

        User user = userService.findById(invoice.getUserId());

        modelAndView.addObject(INVOICE_ATTR, invoice);
        modelAndView.addObject(CART_ATTR, cart);
        modelAndView.addObject(USER_ATTR, user);

        return modelAndView;
    }

    /**
     * @param command
     * @param invoiceId
     * @return
     */
    @PostMapping("/admin/invoices/view")
    public String processInvoice(@RequestParam(value = "command") String command,
                                 @RequestParam(value = "id") String invoiceId
    ) {
        LOG.debug("Try to process invoice id={}, command={}", invoiceId, command);

        switch (command) {
            case APPROVE_CMD:
                LOG.debug("Try to approve invoice");
                invoiceService.approveInvoice(Long.parseLong(invoiceId));
                break;
            case CANCEL_CMD:
                LOG.debug("Try to cancel invoice");
                invoiceService.cancelInvoice(Long.parseLong(invoiceId));
                break;
        }

        return "redirect:" + ADMIN_INVOICES_VIEW_PATH + "?id=" + invoiceId;
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/admin/periodicals/edit")
    public ModelAndView updatePeriodicalView(@RequestParam("id") String id) {
        LOG.debug("Try to show edit periodicals form, periodical id={}", id);

        ModelAndView modelAndView = new ModelAndView(ADMIN_PERIODICALS_EDIT_PAGE);

        Periodical periodical = periodicalService.getById(Long.parseLong(id));

        float price = Float.valueOf(periodical.getMonthlyPrice()) / 100;

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setId(periodical.getId());
        periodicalDto.setName(periodical.getName());
        periodicalDto.setDescription(periodical.getDescription());
        periodicalDto.setMonthlyPrice(price);

        LOG.debug("PeriodicalDto: {}", periodicalDto);

        modelAndView.addObject(PERIODICAL_ATTR, periodicalDto);

        return modelAndView;
    }

    /**
     * @param periodical
     * @param bindingResult
     * @return
     */
    @PostMapping("/admin/periodicals/edit")
    public ModelAndView updatePeriodical(@ModelAttribute("periodical") @Valid PeriodicalDto periodical,
                                         BindingResult bindingResult) {
        LOG.debug("Try to update periodical, periodicalDto: {}", periodical);
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName(ADMIN_PERIODICALS_EDIT_PAGE);
            return modelAndView;
        }

        Periodical periodicalToUpdate = periodicalService.getById(periodical.getId());

        long price = (long) periodical.getMonthlyPrice() * 100;

        periodicalToUpdate.setName(periodical.getName());
        periodicalToUpdate.setDescription(periodical.getDescription());
        periodicalToUpdate.setMonthlyPrice(price);

        periodicalService.update(periodicalToUpdate);

        return new ModelAndView("redirect:" + ADMIN_PERIODICALS_PATH);

    }

    /**
     * @param id
     * @param currentPage
     * @param totalPages
     * @param model
     * @return
     */
    @PostMapping("/admin/periodicals/delete")
    public ModelAndView deletePeriodical(@RequestParam("id") String id,
                                         @RequestParam("currentPage") String currentPage,
                                         @RequestParam("totalPages") String totalPages,
                                         Model model) {
        LOG.debug("Try to delete periodical by id={}", id);

        try {
            periodicalService.deleteById(Long.parseLong(id));
        } catch (EntityEngagedException e) {

            ModelAndView mav = new ModelAndView("admin/periodicals/list-periodicals");

            mav.addObject(PERIODICAL_IN_USE_ATTR, "Periodical In Use");

            int itemsPerPage = Integer.parseInt(env.getProperty("admin_periodicals_per_page"));
            List<Periodical> periodicals = periodicalService.getPeriodicalsPage(Integer.parseInt(currentPage), itemsPerPage);

            mav.addObject(PERIODICALS_ATTR, periodicals);
            mav.addObject(CURRENT_PAGE_ATTR, Integer.parseInt(currentPage));
            mav.addObject(TOTAL_PAGES_ATTR, Integer.parseInt(totalPages));

            return mav;
        }

        return new ModelAndView("redirect:" + ADMIN_PERIODICALS_PATH);

    }

}

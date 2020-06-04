package ua.com.periodicals.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.periodicals.dto.PeriodicalDto;
import ua.com.periodicals.dto.UserDto;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.model.Cart;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserService userService;

    @RequestMapping("/admin/periodicals")
    public ModelAndView getAllPeriodicals(Model model) {
        LOG.debug("Try to show list-periodicals view");

        ModelAndView mav = new ModelAndView("admin/list-periodicals");
        List<Periodical> periodicals = periodicalService.getAllPeriodicals();

        mav.addObject("periodicals", periodicals);
        return mav;
    }

    @RequestMapping("/admin/periodicals/new")
    public ModelAndView showNewPeriodicalForm() {
        LOG.debug("Try to show add periodical form");

        ModelAndView mav = new ModelAndView("admin/add-periodical");

        return mav;
    }

    @PostMapping("/admin/periodicals/new")
    public String savePeriodical(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "price") String price,
                                 @RequestParam(value = "description") String description) {

        LOG.debug("Try to save new periodical: name={}, price={}, description={}", name, price, description);

        Periodical periodical = new Periodical(name, description, Long.parseLong(price));
        periodicalService.save(periodical);

        return "redirect:/admin/periodicals";

    }

    @GetMapping("admin/invoices")
    public ModelAndView listInvoices() {
        LOG.debug("Try to get in progress invoices");
        ModelAndView modelAndView = new ModelAndView("admin/invoice/list-invoices");

        List<Invoice> invoices = invoiceService.getAllUnprocessedInvoices();

        modelAndView.addObject("invoices", invoices);

        return modelAndView;
    }

    @GetMapping("/admin/invoices/view")
    public ModelAndView viewInvoice(@RequestParam(value = "id") String id) {
        LOG.info("Try to show invoice id={}", id);

        ModelAndView modelAndView = new ModelAndView("admin/invoice/invoice");

        Invoice invoice = invoiceService.getById(Long.parseLong(id));
        modelAndView.addObject("invoice", invoice);

        Cart cart = invoiceService.getInvoiceCart(Long.parseLong(id));
        modelAndView.addObject("cart", cart);

        User user = userService.findById(invoice.getUserId());
        modelAndView.addObject("user", user);

        LOG.info("Invoice: {}", invoice);

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

        ModelAndView modelAndView = new ModelAndView("admin/periodical/edit");

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
            modelAndView.setViewName("admin/periodical/edit");
            return modelAndView;
        }

        Periodical periodicalToUpdate = periodicalService.getById(periodical.getId());

        periodicalToUpdate.setName(periodical.getName());
        periodicalToUpdate.setDescription(periodical.getDescription());

        long price = new Float(periodical.getMonthlyPrice() * 100).longValue();
        LOG.debug("Price: {}", price);
        periodicalToUpdate.setMonthlyPrice(price);

        periodicalService.save(periodicalToUpdate);

        return new ModelAndView("redirect:/admin/periodicals");

    }

    @PostMapping("/admin/periodicals/delete")
    public ModelAndView deletePeriodical(@RequestParam("id") String id) {
        LOG.debug("Try to delete periodical by id={}", id);

        periodicalService.deleteById(Long.parseLong(id));

        return new ModelAndView("redirect:/admin/periodicals");

    }


}

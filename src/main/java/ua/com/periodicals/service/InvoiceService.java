package ua.com.periodicals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.model.Cart;

import java.util.List;

@Transactional
@Service
public class InvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    InvoiceDao invoiceDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    UserService userService;

    public List<Invoice> getUnprocessedInvoices() {
        LOG.debug("Try to get unprocessed invoices");
        return invoiceDao.findAllByStatus(Invoice.STATUS.IN_PROGRESS);
    }

    @Transactional
    public boolean submitInvoice(long userId, Cart cart) {
        LOG.debug("Try to submit invoice, userId={}, cart = {}", userId, cart);

        Invoice invoice = new Invoice(userId);
        invoiceDao.save(invoice);

        for (Periodical periodical : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(invoice.getId(), periodical.getId(), periodical.getMonthlyPrice());
            orderItemDao.save(orderItem);
        }

        return true;
    }

    @Transactional
    public boolean approveInvoice(Long invoiceId) {
        LOG.debug("Try to approve invoice, id={}", invoiceId);

        Invoice invoice = invoiceDao.getById(invoiceId);
        User user = userService.findById(invoice.getUserId());
        
        List<Periodical> invoicePeriodicals = periodicalService.findAllByInvoiceId(invoice.getId());

        for (Periodical periodical : invoicePeriodicals) {
            user.getSubscriptions().add(periodical);
        }

        userService.update(user);

        invoice.setStatus(Invoice.STATUS.COMPLETED);
        invoiceDao.update(invoice);

        return true;
    }

    @Transactional
    public void cancelInvoice(Long invoiceId) {
        LOG.debug("Try to cancel invoice, id={}", invoiceId);

        Invoice invoice = invoiceDao.getById(invoiceId);
        invoice.setStatus(Invoice.STATUS.CANCELLED);
        invoiceDao.update(invoice);
    }

    public Invoice getById(long id) {
        LOG.debug("Try to get invoice by id={} ");
        Invoice invoice = invoiceDao.getById(id);
        return invoice;
    }

}

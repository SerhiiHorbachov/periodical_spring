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
import java.util.Set;

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

/*
    @Autowired
    UserPeriodicalsRepository userPeriodicalsRepository;

 */

/*
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

 */


    public List<Invoice> getUnprocessedInvoices() {
        LOG.debug("Try to get unprocessed invoices");
        return invoiceDao.findAllByStatus(Invoice.STATUS.IN_PROGRESS);
    }

    /**/

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
        LOG.info("USER: {}", user);

        List<Periodical> invoicePeriodicals = periodicalService.findAllByInvoiceId(invoice.getId());

//        Set<Periodical> userSubscriptions = user.getSubscriptions();
        for (Periodical periodical : invoicePeriodicals) {
            user.getSubscriptions().add(periodical);
        }

//        user.setSubscriptions(userSubscriptions);

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

    /**/

    public Invoice getById(long id) {
        LOG.debug("Try to get invoice by id={} ");
        Invoice invoice = invoiceDao.getById(id);
        return invoice;
    }


    /*
    public Cart getInvoiceCart(long invoiceId) {
        LOG.info("Try to get cart by invoice");
        List<OrderItem> orderItems = orderItemRepository.findByInvoiceId(invoiceId);

        LOG.info("Invoice id={} items: {}", invoiceId, orderItems);

        Cart cart = new Cart();
        for (OrderItem item : orderItems) {
            Periodical tempPeriodical = periodicalService.getById(item.getPeriodicalId());
            cart.addItem(tempPeriodical);
        }

        LOG.info("Returned Cart: {}", cart);

        return cart;

    }

     */


}

package ua.com.periodicals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.UserPeriodicals;
import ua.com.periodicals.model.Cart;
import ua.com.periodicals.repository.InvoiceRepository;
import ua.com.periodicals.repository.OrderItemRepository;
import ua.com.periodicals.repository.UserPeriodicalsRepository;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class InvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PeriodicalService periodicalService;

    @Autowired
    UserPeriodicalsRepository userPeriodicalsRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getAllUnprocessedInvoices() {
        return invoiceRepository.findAllByStatus(Invoice.STATUS.IN_PROGRESS);
    }

    @Transactional
    public boolean submitInvoice(long userId, Cart cart) {
        Invoice invoice = new Invoice(userId);
        invoice = invoiceRepository.save(invoice);

        for (Periodical periodical : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(invoice.getId(), periodical.getId(), periodical.getMonthlyPrice());

            LOG.info("OrderItem to be saved: {}", orderItem);

            orderItemRepository.save(orderItem);
        }

        return true;
    }

    @Transactional
    public boolean approveInvoice(Long invoiceId) {
        LOG.debug("Try to approve invoice, id={}", invoiceId);

        Optional<Invoice> invoiceCandidate = invoiceRepository.findById(invoiceId);
        Invoice invoice;
        if (invoiceCandidate.get() == null) {
            throw new RuntimeException("Not found");
        } else {
            invoice = invoiceCandidate.get();
        }

        Set<OrderItem> orderItems = invoice.getOrderItems();

        for (OrderItem item : orderItems) {
            userPeriodicalsRepository.save(new UserPeriodicals(invoice.getUserId(), item.getPeriodicalId()));
        }

        LOG.info("orderItems: {}", orderItems);

        invoice.setStatus(Invoice.STATUS.COMPLETED);
        invoiceRepository.save(invoice);

        return true;
    }

    @Transactional
    public boolean cancelInvoice(Long invoiceId) {
        LOG.debug("Try to cancel invoice, id={}", invoiceId);

        Optional<Invoice> invoiceCandidate = invoiceRepository.findById(invoiceId);
        Invoice invoice;
        if (invoiceCandidate.get() == null) {
            throw new RuntimeException("Not found");
        } else {
            invoice = invoiceCandidate.get();
        }

        invoice.setStatus(Invoice.STATUS.CANCELLED);
        invoiceRepository.save(invoice);

        return true;
    }


    public Invoice getById(long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.get() == null) {
            throw new NoSuchElementException(String.format("Invoice with id=%d is not present.", id));
        } else {
            return invoice.get();
        }
    }

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


}

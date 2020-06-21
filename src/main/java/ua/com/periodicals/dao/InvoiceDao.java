package ua.com.periodicals.dao;

import ua.com.periodicals.entity.Invoice;

import java.util.List;

public interface InvoiceDao {
    Invoice save(Invoice invoice);

    List<Invoice> findAllByStatus(Invoice.STATUS status);

    Invoice getById(long id);

    void update(Invoice invoice);

    boolean isPeriodicalInUnpaidInvoice(long userId, long periodicalId);

}

package ua.com.periodicals.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.exception.DaoException;
import ua.com.periodicals.exception.NotFoundException;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Serhii Hor
 * @since 2020-06
 */
@Repository
public class InvoiceDaoImpl implements InvoiceDao {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceDaoImpl.class);

    private static final String FIND_ALL_INVOICES_BY_STATUS = "SELECT\n" +
        "    i \n" +
        "from\n" +
        "    Invoice i \n" +
        "WHERE\n" +
        "    i.status = :status \n" +
        "ORDER BY\n" +
        "    i.id";

    private static final String FIND_PERIODICAL_IN_UNPROCESSED_INVOICE =
        "SELECT\n" +
            "    p.id,\n" +
            "    p.name,\n" +
            "    p.description,\n" +
            "    p.monthly_price_cents \n" +
            "FROM\n" +
            "    invoices inv \n" +
            "JOIN\n" +
            "    order_items o_i \n" +
            "        ON inv.invoice_id = o_i.invoice_id \n" +
            "JOIN\n" +
            "    periodicals p \n" +
            "        ON o_i.periodical_id = p.id \n" +
            "WHERE\n" +
            "    inv.status='IN_PROGRESS' \n" +
            "    AND inv.user_id= :userId \n" +
            "    AND p.id= :periodicalId";

    @Autowired
    SessionFactory sessionFactory;

    /**
     * Saves invoice entity in the database.
     *
     * @param invoice Invoice Entity
     * @return Saved Invoice with generated id.
     */
    @Override
    public Invoice save(Invoice invoice) {
        LOG.debug("Try to save invoice: {}", invoice);

        try (Session session = sessionFactory.openSession()) {

            try {
                session.beginTransaction();

                long id = (long) session.save(invoice);
                Invoice storedInvoice = session.get(Invoice.class, id);

                session.getTransaction().commit();

                return storedInvoice;
            } catch (HibernateException e) {
                LOG.error("Failed to save new invoice: {}", invoice, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to save new invoice", e);
            }
        }

    }

    /**
     * Finds all invoice by status
     *
     * @param status Invoice.STATUS
     * @return A List of Invoices with matching status
     */
    @Override
    public List<Invoice> findAllByStatus(Invoice.STATUS status) {
        LOG.debug("Try to find all invoices by status: {}", status);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                List<Invoice> invoices = session
                    .createQuery(FIND_ALL_INVOICES_BY_STATUS, Invoice.class)
                    .setParameter("status", status)
                    .getResultList();

                session.getTransaction().commit();

                return invoices;
            } catch (HibernateException e) {
                LOG.error("Failed to find all invoices by status: {}", status, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to find all invoices by status", e);
            }
        }

    }

    /**
     * Finds Invoice by its id.
     *
     * @param id invoice id
     * @return Invoice
     */
    @Override
    public Invoice getById(long id) {
        LOG.debug("Try to get invoice by id={}", id);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                Invoice invoice = session.get(Invoice.class, id);
                session.getTransaction().commit();

                if (invoice == null) {
                    LOG.warn("Invoice with id={} is not found", id);
                    throw new NotFoundException(String.format("Invoice with id=%d is not found", id));
                } else {
                    return invoice;
                }
            } catch (HibernateException e) {
                LOG.error("Failed to find invoice by id: {}", id, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to find all invoices by status", e);
            }
        }

    }

    /**
     * Updates invoice in the database
     *
     * @param invoice
     * @return void
     */
    @Override
    public void update(Invoice invoice) {
        LOG.debug("Try to update invoice: {}", invoice);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.update(invoice);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                LOG.error("Failed to update invoice: {}", invoice, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to update invoice", e);
            }
        }

    }

    /**
     * Checks if a periodical is currently in invoices with status IN_PROGRESS
     *
     * @param userId       user id
     * @param periodicalId periodical id
     * @return
     */
    @Override
    public boolean isPeriodicalInUnpaidInvoice(long userId, long periodicalId) {
        LOG.debug("Try to check if periodical is in unpaid invoice: userId={}, periodicalId={}", userId, periodicalId);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Query query = session.createSQLQuery(FIND_PERIODICAL_IN_UNPROCESSED_INVOICE)
                    .setParameter("userId", userId)
                    .setParameter("periodicalId", periodicalId)
                    .addEntity(Periodical.class);

                query.getSingleResult();

                session.getTransaction().commit();

            } catch (NoResultException e) {
                LOG.warn("User id={}, doesn't have periodical id={} in unprocessed invoices", userId, periodicalId, e);
                session.getTransaction().rollback();
                return false;
            } catch (HibernateException e) {
                LOG.error("Failed to find periodical in unprocessed invoices", e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to find periodical in unprocessed invoices", e);
            }
        }

        return true;
    }
}

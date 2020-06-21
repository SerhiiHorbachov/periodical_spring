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
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvoiceDaoImpl implements InvoiceDao {

    private final static String FIND_PERIODICAL_IN_UNPROCESSED_INVOICE =
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

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalDaoImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Invoice save(Invoice invoice) {
        LOG.debug("Try to save invoice: {}", invoice);

        Invoice storedInvoice = null;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            long id = (long) session.save(invoice);
            storedInvoice = session.get(Invoice.class, id);

            session.getTransaction().commit();

            return storedInvoice;
        } catch (HibernateException e) {
            LOG.error("Failed to save new invoice: {}", invoice, e);
            throw new DaoException("Failed to save new invoice", e);
        }

    }

    @Override
    public List<Invoice> findAllByStatus(Invoice.STATUS status) {
        LOG.debug("Try to find all invoices by status: {}", status);

        List<Invoice> invoices = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            invoices = session
                .createQuery("SELECT i from Invoice i WHERE i.status = :status ORDER BY i.id", Invoice.class)
                .setParameter("status", status)
                .getResultList();

            session.getTransaction().commit();

            return invoices;
        } catch (HibernateException e) {
            LOG.error("Failed to find all invoices by status: {}", status, e);
            throw new DaoException("Failed to find all invoices by status", e);
        }

    }

    @Override
    public Invoice getById(long id) {
        LOG.debug("Try to get invoice by id={}", id);

        try (Session session = sessionFactory.openSession()) {
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
            throw new DaoException("Failed to find all invoices by status", e);
        }

    }

    @Override
    public void update(Invoice invoice) {
        LOG.debug("Try to update invoice: {}", invoice);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(invoice);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            LOG.error("Failed to update invoice: {}", invoice, e);
            throw new DaoException("Failed to update invoice", e);
        }

    }

    @Override
    public boolean isPeriodicalInUnpaidInvoice(long userId, long periodicalId) {
        LOG.debug("Try to check if periodical is in unpaid invoice: userId={}, periodicalId={}", userId, periodicalId);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Query query = session.createSQLQuery(FIND_PERIODICAL_IN_UNPROCESSED_INVOICE)
                .setParameter("userId", userId)
                .setParameter("periodicalId", periodicalId)
                .addEntity(Periodical.class);

            query.getSingleResult();

            session.getTransaction().commit();

        } catch (NoResultException e) {
            LOG.warn("User id={}, doesn't have periodical id={} in unprocessed invoices", userId, periodicalId, e);
            return false;
        } catch (HibernateException e) {
            LOG.error("Failed to find periodical in unprocessed invoices", e);
            throw new DaoException("Failed to find periodical in unprocessed invoices", e);
        }

        return true;
    }
}

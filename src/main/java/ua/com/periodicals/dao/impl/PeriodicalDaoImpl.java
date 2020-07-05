package ua.com.periodicals.dao.impl;

import ch.qos.logback.classic.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.PeriodicalDao;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.exception.DaoException;
import ua.com.periodicals.exception.NotFoundException;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class PeriodicalDaoImpl implements PeriodicalDao {

    private final static String FIND_ALL_PERIODICALS_TYPED_QUERY = "SELECT p FROM Periodical p";
    private final static String FIND_ALL_PERIODICALS_BY_INVOICE_ID =
        "select\n" +
            "    p.id,\n" +
            "    p.name,\n" +
            "    p.description,\n" +
            "    o_i.price_per_month as monthly_price_cents\n" +
            "from\n" +
            "    order_items o_i \n" +
            "JOIN\n" +
            "    periodicals p \n" +
            "        ON o_i.periodical_id=p.id \n" +
            "WHERE\n" +
            "    invoice_id= :invoiceId";

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(PeriodicalDaoImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Periodical> findAll() {
        LOG.debug("Try to find all periodicals");

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                TypedQuery<Periodical> typedQuery = session
                    .createQuery(FIND_ALL_PERIODICALS_TYPED_QUERY, Periodical.class);
                List<Periodical> periodicals = typedQuery.getResultList();

                session.getTransaction().commit();

                return periodicals;

            } catch (HibernateException e) {
                LOG.error("Failed to find all periodicals: ", e);
                session.getTransaction().rollback();
                throw new DaoException("Error when getting all periodicals.", e);
            }
        }

    }

    public List<Periodical> findPerPage(int firstResult, int maxResults) {
        LOG.debug("Try to find periodicals per page: firstResult={}, maxResult={}", firstResult, maxResults);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Query query = session.createQuery("FROM Periodical p ORDER BY p.id");
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
                List<Periodical> periodicals = query.getResultList();

                session.getTransaction().commit();

                return periodicals;
            } catch (HibernateException e) {
                LOG.error("Failed to find all periodicals per page. FirstResult={}, maxResult={} ", firstResult, maxResults, e);
                session.getTransaction().rollback();
                throw new DaoException(String.format("Error when getting periodicals per page, firstResult=%d, maxResult=%d", firstResult, maxResults), e);
            }
        }
    }

    @Override
    @Transactional
    public Periodical save(Periodical periodical) {
        LOG.debug("Try to save new periodical: {}", periodical);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                long id = (long) session.save(periodical);

                Periodical storedPeriodical = session.get(Periodical.class, id);
                session.getTransaction().commit();

                return storedPeriodical;
            } catch (HibernateException e) {
                LOG.error("Failed to save new periodical: {}", periodical, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to save new periodical", e);
            }
        }

    }

    @Override
    public Periodical getById(long id) {
        LOG.debug("Try to find periodical by id={}", id);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Periodical periodical = session.get(Periodical.class, id);

                session.getTransaction().commit();

                if (periodical != null) {
                    return periodical;
                } else {
                    LOG.warn("Periodical id={} is not present. ", id);
                    throw new NotFoundException(String.format("Periodical id=%d is not present", id));
                }

            } catch (HibernateException e) {
                LOG.error("Failed to get periodical by id={}", id, e);
                session.getTransaction().rollback();
                throw new DaoException(String.format("Failed to get periodical by id=%d", id), e);
            }
        }

    }

    @Override
    @Transactional
    public Periodical update(Periodical periodical) {
        LOG.debug("Try to update periodical: {}", periodical);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                session.update(periodical);
                Periodical updatedPeriodical = session.get(Periodical.class, periodical.getId());

                session.getTransaction().commit();

                return updatedPeriodical;
            } catch (HibernateException e) {
                LOG.error("Failed to update periodical: {}", periodical, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to update periodical", e);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {

        LOG.debug("Try to delete periodical, id={}", id);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Periodical periodical = session.get(Periodical.class, id);
                session.remove(periodical);

                session.getTransaction().commit();
            } catch (HibernateException e) {
                LOG.error("Failed to delete periodical, id={}", id, e);
                session.getTransaction().rollback();
                throw new DaoException(String.format("Failed to delete periodical, id=%d", id), e);
            }
        }

    }

    @Override
    public long getCount() {
        LOG.debug("Try to get count of periodicals.");

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Query query = session.createQuery("select count(*) from Periodical");
                Long size = (Long) query.getSingleResult();

                session.getTransaction().commit();

                return size;
            } catch (HibernateException e) {
                LOG.error("Failed to get count from periodicals", e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to get count from periodicals", e);
            }
        }

    }

    @Override
    public List<Periodical> findAllByInvoiceId(long id) {
        LOG.debug("Try to get all periodicals by invoice id={}", id);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Query query = session
                    .createSQLQuery(FIND_ALL_PERIODICALS_BY_INVOICE_ID).
                        setParameter("invoiceId", id)
                    .addEntity(Periodical.class);

                List<Periodical> periodicals = query.getResultList();

                session.getTransaction().commit();

                return periodicals;

            } catch (HibernateException e) {
                LOG.error("Failed to get all periodicals by invoice id={}", id, e);
                session.getTransaction().rollback();
                throw new DaoException(String.format("Failed to get all periodicals by invoice id=%d", id), e);
            }
        }
    }



}

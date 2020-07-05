package ua.com.periodicals.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.exception.DaoException;

import java.util.List;

@Repository
@Transactional
public class OrderItemDaoImpl implements OrderItemDao {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemDaoImpl.class);

    private final static String FIND_PERIODICALS_IN_ORDER_ITEMS = "SELECT\n" +
        "    i \n" +
        "from\n" +
        "    OrderItem i \n" +
        "WHERE\n" +
        "    i.periodicalId = :periodicalId";

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public OrderItem save(OrderItem item) {
        LOG.debug("Try to save orderItem: {}", item);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                long id = (long) session.save(item);
                OrderItem storedItem = session.get(OrderItem.class, id);

                session.getTransaction().commit();

                return storedItem;
            } catch (
                HibernateException e) {
                LOG.error("Failed to save new order item: {}", item, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to save new order item.", e);
            }
        }

    }

    @Override
    public boolean isPeriodicalInOrderItems(long periodicalId) {

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();


                List<OrderItem> periodicals = session
                    .createQuery(FIND_PERIODICALS_IN_ORDER_ITEMS, OrderItem.class)
                    .setParameter("periodicalId", periodicalId)
                    .getResultList();

                session.getTransaction().commit();

                return periodicals.iterator().hasNext();

            } catch (HibernateException e) {
                LOG.error("Failed to check if periodical id={} in order items. ", e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to save new order item.", e);
            }
        }

    }
}

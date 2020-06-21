package ua.com.periodicals.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.exception.DaoException;

@Repository
@Transactional
public class OrderItemDaoImpl implements OrderItemDao {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemDaoImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public OrderItem save(OrderItem item) {
        LOG.debug("Try to save orderItem: {}", item);

        OrderItem storedItem;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            long id = (long) session.save(item);
            storedItem = session.get(OrderItem.class, id);

            session.getTransaction().commit();

            return storedItem;
        } catch (
            HibernateException e) {
            LOG.error("Failed to save new order item: {}", item, e);
            throw new DaoException("Failed to save new order item.", e);
        }

    }
}

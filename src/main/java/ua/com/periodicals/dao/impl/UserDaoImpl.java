package ua.com.periodicals.dao.impl;

import ch.qos.logback.classic.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.com.periodicals.dao.UserDao;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.exception.DaoException;
import ua.com.periodicals.exception.NotFoundException;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(UserDaoImpl.class);

    private final static String FIND_USER_BY_ID_TYPED_QUERY = "select u from User u where u.email=:email";
    private final static String FIND_SUBSCRIBED_PERIODICAL_QUERY =
        "SELECT\n" +
            "    p.id,\n" +
            "    p.name,\n" +
            "    p.description,\n" +
            "    p.monthly_price_cents \n" +
            "FROM\n" +
            "    periodicals p \n" +
            "JOIN\n" +
            "    users_periodicals u_p \n" +
            "        ON p.id = u_p.periodical_id \n" +
            "WHERE\n" +
            "    user_id= :userId \n" +
            "    AND periodical_id= :periodicalId";

    private static final String FIND_USER_BY_ID_QUERY = "SELECT\n" +
        "    u \n" +
        "FROM\n" +
        "    User u \n" +
        "LEFT JOIN\n" +
        "    FETCH u.subscriptions \n" +
        "WHERE\n" +
        "    u.id = :id";

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Optional<User> findByEmail(String email) {
        LOG.debug("Try to find user by email {}", email);

        User user = null;

        try (Session session = sessionFactory.openSession()) {
            try {
                TypedQuery<User> typedQuery = session.createQuery(FIND_USER_BY_ID_TYPED_QUERY, User.class);
                typedQuery.setParameter("email", email);

                session.beginTransaction();
                user = typedQuery.getSingleResult();
                session.getTransaction().commit();

            } catch (NoResultException e) {
                LOG.warn("User with email {} is not present: ", email);
                session.getTransaction().rollback();
            } catch (HibernateException e) {
                LOG.error("Failed to find user by email: {}", email, e);
                session.getTransaction().rollback();
                throw new DaoException("Error when finding user by email: " + email, e);
            }
        }

        return Optional.ofNullable(user);

    }

    @Override
    public User findById(long id) {
        LOG.debug("Try to find user by id={}", id);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Query query = session.createQuery(FIND_USER_BY_ID_QUERY);
                query.setParameter("id", id);

                User user = (User) query.getSingleResult();
                session.getTransaction().commit();

                return user;

            } catch (NoResultException e) {
                LOG.warn("User with id {} is not present.", id, e);
                session.getTransaction().rollback();
                throw new NotFoundException(String.format("User with id=%d is not present.", id));
            } catch (HibernateException e) {
                LOG.error("Failed to find user by id={}", id, e);
                session.getTransaction().rollback();
                throw new DaoException("Error when finding user by id=" + id, e);
            }
        }

    }

    @Override
    public boolean isUserSubscribedToPeriodical(long userId, long periodicalId) {
        LOG.debug("Try to check if user id={} is subscribed to periodical id={}", userId, periodicalId);

        try (Session session = sessionFactory.openSession()) {
            try {

                session.beginTransaction();

                Query query = session.createSQLQuery(FIND_SUBSCRIBED_PERIODICAL_QUERY)
                    .setParameter("userId", userId)
                    .setParameter("periodicalId", periodicalId)
                    .addEntity(Periodical.class);

                query.getSingleResult();
                session.getTransaction().commit();

                return true;
            } catch (NoResultException e) {
                LOG.warn("User id={} is not subscribed to periodical id={}", userId, periodicalId, e);
                session.getTransaction().rollback();
                return false;
            } catch (HibernateException e) {
                LOG.error("Failed to check if user id={} is subscribed to periodical id={}", userId, periodicalId, e);
                session.getTransaction().rollback();
                throw new DaoException(String.format("Failed to check if user id=%d is subscribed to periodical id=%d", userId, periodicalId), e);
            }
        }

    }

    @Override
    public void update(User user) {
        LOG.debug("Try to update user: {}", user);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                session.update(user);
                session.getTransaction().commit();

            } catch (HibernateException e) {
                LOG.error("Failed to update user: {}", user, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to update user.", e);
            }
        }

    }

    @Override
    public User save(User user) {
        LOG.debug("Try to save new user: {}", user);

        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                long id = (long) session.save(user);

                User storedUser = session.get(User.class, id);

                session.getTransaction().commit();

                return storedUser;
            } catch (HibernateException e) {
                LOG.error("Failed to save new user: {}", user, e);
                session.getTransaction().rollback();
                throw new DaoException("Failed to save new user", e);
            }
        }
    }

}

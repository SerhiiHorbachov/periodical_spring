package ua.com.periodicals.service;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.dao.PeriodicalDao;
import ua.com.periodicals.dao.UserDao;
import ua.com.periodicals.dto.UserDto;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.exception.DuplicateRecordException;
import ua.com.periodicals.security.MyUserDetailsService;

/**
 * @author Serhii Hor
 * @since 2020-06
 */
@Service
public class UserService {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private PeriodicalDao periodicalDao;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public void update(User user) {
        userDao.update(user);
    }

    /**
     * Method creates a new user in the database and encodes password.
     *
     * @param userDto
     * @return registered user with generated Id.
     */
    @Transactional
    public User register(UserDto userDto) {
        LOG.debug("Try to save user: {}", userDto);

        if (isUserPresent(userDto.getEmail())) {
            LOG.warn("User [{}] already registered", userDto.getEmail());
            throw new DuplicateRecordException("User already exists");
        }

        User userToSave = new User();
        userToSave.setFirstName(userDto.getFirstName());
        userToSave.setLastName(userDto.getLastName());
        userToSave.setEmail(userDto.getEmail());
        userToSave.setPwd(passwordEncoder.encode(userDto.getPwd()));
        userToSave.setRole(User.Role.USER);

        return userDao.save(userToSave);
    }

    /**
     * Checks if user with the specified email is registered.
     *
     * @param email user email.
     * @return boolean
     */
    public boolean isUserPresent(String email) {
        LOG.debug("Try to check if user present: {}", email);
        return userDao.findByEmail(email).isPresent() ? true : false;
    }

    /**
     * Checks if User is subscribed to the specified Periodical
     *
     * @param userId       user id
     * @param periodicalId periodical id
     * @return boolean
     */
    public boolean isUserSubscribedToPeriodical(long userId, long periodicalId) {
        LOG.info("Try to check if user is subscribed to periodicals, userId={}, periodicalId={}", userId, periodicalId);
        return userDao.isUserSubscribedToPeriodical(userId, periodicalId);
    }

    /**
     * Check whether Periodical is in Invoice with status IN_PROGRESS of the specified User.
     *
     * @param userId       user id.
     * @param periodicalId periodicals id.
     * @return boolean.
     */
    public boolean isPeriodicalInUnpaidInvoice(long userId, long periodicalId) {
        return invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId);
    }

    /**
     * Removes periodical from User's subscriptions in database.
     *
     * @param periodicalId periodicals id.
     * @param userId       user id.
     */
    @Transactional
    public void unsubscribe(long periodicalId, long userId) {
        LOG.debug("Try to get delete subscription");

        Periodical periodical = periodicalDao.getById(periodicalId);
        User user = myUserDetailsService.getLoggedUser();

        user.getSubscriptions().remove(periodical);

        userDao.update(user);

    }

}

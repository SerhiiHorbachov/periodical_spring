package ua.com.periodicals.service;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ua.com.periodicals.security.UserPrincipal;

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

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public void update(User user) {
        userDao.update(user);
    }

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

    public boolean isUserPresent(String email) {
        LOG.debug("Try to check if user present: {}", email);
        return userDao.findByEmail(email).isPresent() ? true : false;
    }

    public boolean isUserSubscribedToPeriodical(long userId, long periodicalId) {
        LOG.info("Try to check if user is subscribed to periodicals, userId={}, periodicalId={}", userId, periodicalId);
        return userDao.isUserSubscribedToPeriodical(userId, periodicalId);
    }

    public User getLoggedUser() {
        LOG.debug("Try to get logged user");
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDao.findById(principal.getId());
    }

    public boolean isPeriodicalInUnpaidInvoice(long userId, long periodicalId) {
        return invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId);
    }

    @Transactional
    public void unsubscribe(long periodicalId) {
        LOG.debug("Try to get delete subscription");

        Periodical periodical = periodicalDao.getById(periodicalId);
        User user = getLoggedUser();

        user.getSubscriptions().remove(periodical);

        userDao.update(user);

    }

}

package ua.com.periodicals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dto.UserDto;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.entity.UserPeriodicals;
import ua.com.periodicals.exception.DuplicateRecordException;
import ua.com.periodicals.repository.UserPeriodicalsRepository;
import ua.com.periodicals.repository.UserRepository;
import ua.com.periodicals.security.UserPrincipal;

import java.util.*;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPeriodicalsRepository userPeriodicalsRepository;

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.size() > 0) {
            return users;
        } else {
            return new ArrayList<User>();
        }
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.get() == null) {
            throw new NoSuchElementException(String.format("User with id=%d not found"));
        } else {
            return user.get();
        }
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

        return userRepository.save(userToSave);
    }

    public boolean isUserPresent(String email) {
        LOG.debug("Try to check if user present: {}", email);

        User user = userRepository.findByEmail(email);

        return user != null ? true : false;

    }

    public boolean isUserSubscribedToPeriodical(long userId, long periodicalId) {
        LOG.info("Try to check if user is subscribed to periodicals, userId={}, periodicalId={}", userId, periodicalId);

        UserPeriodicals userPeriodicals = userPeriodicalsRepository.findByUserIdAndAndPeriodicalId(userId, periodicalId);
        LOG.info("UserPeriodicals: {}", userPeriodicals);

        return userPeriodicals != null ? true : false;
    }

    public User getLoggedUser() {
        LOG.info("Try to get logged user");

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(() -> new IllegalArgumentException("Not found"));

        return user;

    }

    public Set<Periodical> getActiveSubscriptions() {
        LOG.info("Try to get active subscriptions");

        User user = getLoggedUser();
        return user.getSubscriptions();
    }

    @Transactional
    public boolean unsubscribe(long userId, long periodicalId) {
        LOG.debug("Try to get delete subscription");
        return userPeriodicalsRepository.deleteByUserIdAndAndPeriodicalId(userId, periodicalId) > 0;
    }

}

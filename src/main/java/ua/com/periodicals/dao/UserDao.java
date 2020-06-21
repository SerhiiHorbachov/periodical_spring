package ua.com.periodicals.dao;

import ua.com.periodicals.entity.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByEmail(String email);

    User findById(long id);

    boolean isUserSubscribedToPeriodical(long userId, long periodicalId);

    void update(User user);

    User save(User user);

}

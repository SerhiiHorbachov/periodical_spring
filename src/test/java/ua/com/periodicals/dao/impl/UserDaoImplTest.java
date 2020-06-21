package ua.com.periodicals.dao.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.periodicals.config.HibernateConfig;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.exception.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserDaoImplTest {

    private static ApplicationContext applicationContext;

    private static UserDaoImpl userDao;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(HibernateConfig.class);
        userDao = applicationContext.getBean(UserDaoImpl.class);
        applicationContext.getBean("PopulateData");
    }

    @Test
    void findByEmail_ShouldReturnOptionalAndContainStudentFoundByEmail() {
        String validEmail = "jack.nich@gmai.com";
        Optional<User> user = userDao.findByEmail(validEmail);
        Assertions.assertNotNull(user.get());
    }

    @Test
    void findById_ShouldReturnUserWithMatchingId() {
        long userId = 1;
        User user = userDao.findById(userId);
        Assertions.assertNotNull(user);
    }


    @Test
    void findById_ShouldThrowNotFoundException_WhenUserIdDoesntExist() {
        long invalidUserId = 0;
        Assertions.assertThrows(NotFoundException.class, () -> userDao.findById(invalidUserId));
    }

    @Test
    void findById_ShouldReturnUser_ContainingHisSubscriptions() {
        long userId = 2;
        User user = userDao.findById(userId);

        Assertions.assertNotNull(user.getSubscriptions());
        Assertions.assertEquals(4, user.getSubscriptions().size());
    }

    @Test
    void isUserSubscribedToPeriodical_ShouldReturnTrue_WhenUserIsSubscribedToPeriodical() {
        long userId = 2;
        long periodicalId = 3;
        assertTrue(userDao.isUserSubscribedToPeriodical(userId, periodicalId));
    }

    @Test
    void isUserSubscribedToPeriodical_ShouldReturnFalse_WhenUserIsSubscribedToPeriodical() {
        long userId = 2;
        long notSubscribedPeriodicalId = 19;
        assertFalse(userDao.isUserSubscribedToPeriodical(userId, notSubscribedPeriodicalId));
    }

    @Test
    void update_ShouldUpdateExistingUser() {
        long userId = 3;
        String expectedNameBeforeUpdate = "Robert";
        String expectedNameAfterUpdate = "Bob";

        User user = userDao.findById(userId);

        String actualNameBeforeUpdate = user.getFirstName();
        user.setFirstName(expectedNameAfterUpdate);

        userDao.update(user);

        assertAll(
            () -> assertEquals(expectedNameBeforeUpdate, actualNameBeforeUpdate),
            () -> assertEquals(expectedNameAfterUpdate, userDao.findById(userId).getFirstName())
        );

    }

    @Test
    void save_ShouldCreateNewUserInDatabase() {
        String fName = "John";
        String lName = "Smith";
        User.Role role = User.Role.USER;
        String email = "john_smith@testmail.com";
        String pwd = "password";
        long expectedUserId = 6;

        User userToStore = new User(fName, lName, role, email, pwd);

        userToStore = userDao.save(userToStore);

        assertEquals(expectedUserId, userToStore.getId());

    }


}

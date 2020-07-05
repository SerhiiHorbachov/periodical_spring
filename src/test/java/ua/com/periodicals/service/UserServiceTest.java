package ua.com.periodicals.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.dao.PeriodicalDao;
import ua.com.periodicals.dao.UserDao;
import ua.com.periodicals.dto.UserDto;
import ua.com.periodicals.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserDao userDao;

    @Mock
    InvoiceDao invoiceDao;

    @Mock
    PeriodicalDao periodicalDao;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;


    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void findById_ShouldReturnUserWithMatchingId() {
        long userId = 1;
        User mockedUser = new User(userId, "John", "Smith", User.Role.USER, "test@testmail.com", "password");

        when(userDao.findById(userId)).thenReturn(mockedUser);
        assertEquals(mockedUser, userDao.findById(userId));
    }

    @Test
    void register_ShouldReturnUserStoredUser() {
        long userId = 1;
        String pwdHash = "$2y$10$nqFS0AbALQCijxBGa9jMw.gWfj4wCB0UfUnOkeemA.SGrKznOg06u";
        UserDto userDto = new UserDto("John", "Smith", "test@testmail.com", "password");

        User mockedUser = new User(userId, "John", "Smith", User.Role.USER, "test@testmail.com", pwdHash);

        when(passwordEncoder.encode(userDto.getPwd())).thenReturn(pwdHash);
        when(userDao.save(any(User.class))).thenReturn(mockedUser);
        assertEquals(mockedUser, userService.register(userDto));
    }

    @Test
    void isUserPresent_ShouldReturnTrueIsEmailIsStoredInDatabase() {
        String email = "user@testmail.com";
        User user = new User(1, "John", "Smith", User.Role.USER, email, "password");
        Optional<User> userOptional = Optional.ofNullable(user);

        when(userDao.findByEmail(email)).thenReturn(userOptional);

        assertEquals(true, userService.isUserPresent(email));
    }

    @Test
    void isUserPresent_ShouldReturnFalseIsEmailNotInDatabase() {
        String notRegisteredEmail = "user@testmail.com";
        User user = null;
        Optional<User> userOptional = Optional.ofNullable(user);

        when(userDao.findByEmail(notRegisteredEmail)).thenReturn(userOptional);

        assertEquals(false, userService.isUserPresent(notRegisteredEmail));
    }

    @Test
    void isUserSubscribedToPeriodical_ShouldReturnTrueWhenUserIsSubscribed() {
        long userId = 1;
        long periodicalId = 2;

        when(userDao.isUserSubscribedToPeriodical(userId, periodicalId)).thenReturn(true);

        assertTrue(userService.isUserSubscribedToPeriodical(userId, periodicalId));
    }

    @Test
    void isUserSubscribedToPeriodical_ShouldReturnFalseWhenUserIsNotSubscribed() {
        long userId = 1;
        long periodicalId = 2;

        when(userDao.isUserSubscribedToPeriodical(userId, periodicalId)).thenReturn(false);

        assertFalse(userService.isUserSubscribedToPeriodical(userId, periodicalId));
    }

    @Test
    void isPeriodicalInUnpaidInvoice_ShouldReturnTrue_WhenPeriodicalsIsInUnpaidInvoices() {
        long userId = 1;
        long periodicalId = 2;

        when(invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId)).thenReturn(true);

        assertTrue(userService.isPeriodicalInUnpaidInvoice(userId, periodicalId));
    }

    @Test
    void isPeriodicalInUnpaidInvoice_ShouldReturnFalse_WhenPeriodicalsIsInUnpaidInvoices() {
        long userId = 1;
        long periodicalId = 2;

        when(invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId)).thenReturn(false);

        assertFalse(userService.isPeriodicalInUnpaidInvoice(userId, periodicalId));
    }

}

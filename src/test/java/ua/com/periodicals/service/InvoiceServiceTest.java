package ua.com.periodicals.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.periodicals.config.AppSecurityConfig;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.model.Cart;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    private static ApplicationContext applicationContext;

    @Mock
    InvoiceDao invoiceDao;

    @Mock
    OrderItemDao orderItemDao;

    @Mock
    UserService userService;

    @Mock
    PeriodicalService periodicalService;

    @InjectMocks
    private static InvoiceService invoiceService;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(AppSecurityConfig.class);
        invoiceService = applicationContext.getBean(InvoiceService.class);
    }

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void getById() {
        Invoice invoiceMock = new Invoice();
        when(invoiceDao.getById(1)).thenReturn(invoiceMock);
        assertNotNull(invoiceService.getById(1));
    }

    @Test
    void getUnprocessedInvoices_ShouldReturnAllUnprocessedInvoices() {
        int expectedSize = 10;
        List<Invoice> unprocessedInvoicesMock = new ArrayList<>();

        for (int i = 0; i < expectedSize; i++) {
            unprocessedInvoicesMock.add(new Invoice());
        }

        when(invoiceDao.findAllByStatus(Invoice.STATUS.IN_PROGRESS)).thenReturn(unprocessedInvoicesMock);

        assertEquals(expectedSize, invoiceService.getUnprocessedInvoices().size());
    }

    @Test
    void submitInvoice_ShouldReturnTrueWhenInvoiceIsSubmitted() {
        long userId = 1;

        Invoice invoiceMock = new Invoice();
        invoiceMock.setId(1);
        invoiceMock.setStatus(Invoice.STATUS.IN_PROGRESS);
        invoiceMock.setUserId(userId);

        Cart cart = new Cart();
        Periodical periodical1 = new Periodical(1, "periodical1", "", 1000);
        Periodical periodical2 = new Periodical(2, "periodical2", "", 1000);
        cart.addItem(periodical1);
        cart.addItem(periodical2);

        when(invoiceDao.save(any(Invoice.class))).thenReturn(invoiceMock);
        when(orderItemDao.save(any(OrderItem.class))).thenReturn(new OrderItem());

        assertTrue(invoiceService.submitInvoice(userId, cart));
    }

    @Test
    void approveInvoice_ShouldReturnTrueWhenInvoiceSubmittedSuccessfully() {

        long invoiceId = 1;
        long userId = 2;

        Invoice invoiceMock = new Invoice();
        invoiceMock.setId(invoiceId);
        invoiceMock.setUserId(userId);
        invoiceMock.setStatus(Invoice.STATUS.IN_PROGRESS);
        invoiceMock.setCreatedAt(Timestamp.valueOf("2020-04-10 20:36:56"));

        User userMock = new User(userId, "First", "Last", User.Role.USER, "email.com", "password");

        List<Periodical> invoicePeriodicalsMock = new ArrayList<>();
        invoicePeriodicalsMock.add(new Periodical(1, "periodical1", "", 1000));
        invoicePeriodicalsMock.add(new Periodical(2, "periodical2", "", 1000));

        when(invoiceDao.getById(invoiceId)).thenReturn(invoiceMock);
        when(userService.findById(userId)).thenReturn(userMock);
        when(periodicalService.findAllByInvoiceId(invoiceId)).thenReturn(invoicePeriodicalsMock);

        assertTrue(invoiceService.approveInvoice(invoiceId));

    }

    @Test
    void cancelInvoice() {
        long invoiceId = 1;
        long userId = 2;

        Invoice invoiceMock = new Invoice();
        invoiceMock.setId(invoiceId);
        invoiceMock.setUserId(userId);
        invoiceMock.setStatus(Invoice.STATUS.IN_PROGRESS);
        invoiceMock.setCreatedAt(Timestamp.valueOf("2020-04-10 20:36:56"));

        when(invoiceDao.getById(invoiceId)).thenReturn(invoiceMock);

        invoiceService.cancelInvoice(invoiceId);
    }

}

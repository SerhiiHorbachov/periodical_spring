package ua.com.periodicals.dao.impl;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.com.periodicals.config.HibernateConfig;
import ua.com.periodicals.dao.InvoiceDao;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceDaoImplTest {

    private static ApplicationContext applicationContext;
    private static InvoiceDao invoiceDao;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(HibernateConfig.class);
        invoiceDao = applicationContext.getBean(InvoiceDaoImpl.class);
        applicationContext.getBean("PopulateData");
    }

    @Test
    void getById_ShouldReturnInvoiceById() {
        long invoiceId = 1;
        Invoice invoice = invoiceDao.getById(invoiceId);
        assertNotNull(invoice);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenInvoiceIdNotInDatabase() {
        long invoiceId = 113000;
        assertThrows(NotFoundException.class, () -> invoiceDao.getById(invoiceId));
    }

    @Test
    void update_ShouldUpdatedInvoiceChangedFields() {
        long invoiceId = 8;
        Invoice.STATUS expectedStatusBeforeUpdate = Invoice.STATUS.IN_PROGRESS;
        Invoice.STATUS expectedStatusAfterUpdate = Invoice.STATUS.COMPLETED;

        Invoice invoice = invoiceDao.getById(invoiceId);
        Invoice.STATUS actualStatusBeforeUpdate = invoice.getStatus();
        invoice.setStatus(Invoice.STATUS.COMPLETED);

        invoiceDao.update(invoice);

        assertAll(
            () -> assertEquals(expectedStatusBeforeUpdate, actualStatusBeforeUpdate),
            () -> assertEquals(expectedStatusAfterUpdate, invoiceDao.getById(invoiceId).getStatus())
        );
    }

    @Test
    void save_ShouldStoreNewInvoiceInDatabaseWithStatusInProgress() {

        long userId = 2;
        long expectedInvoiceId = 12;
        Invoice.STATUS expectedStatus = Invoice.STATUS.IN_PROGRESS;

        Invoice invoice = new Invoice(userId);
        Invoice storedInvoice = invoiceDao.save(invoice);

        assertAll(
            () -> assertNotNull(storedInvoice),
            () -> assertEquals(expectedStatus, storedInvoice.getStatus()),
            () -> assertEquals(expectedInvoiceId, storedInvoice.getId())
        );
    }


    @Test
    @Order(1)
    void findAllByStatus_ShouldReturnListOfInvoicesWithStatusIN_PROGRESS_WhenRequestedToGetAllInProgressInvoices() {
        int expectedSize = 4;
        assertEquals(expectedSize, invoiceDao.findAllByStatus(Invoice.STATUS.IN_PROGRESS).size());
    }

    @Test
    @Order(2)
    void findAllByStatus_ShouldReturnListOfInvoicesWithStatusCOMPLETED_WhenRequestedToGetAllCOMPLETEDInvoices() {
        int expectedSize = 7;
        assertEquals(expectedSize, invoiceDao.findAllByStatus(Invoice.STATUS.COMPLETED).size());
    }

    @Test
    void isPeriodicalInUnpaidInvoice_ShouldReturnTrue_WhenPeriodicalIsInInvoicesWithStatus_IN_PROGRESS() {
        long userId = 2;
        long periodicalId = 16;
        assertTrue(invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId));

    }

    @Test
    void isPeriodicalInUnpaidInvoice_ShouldReturnFalse_WhenPeriodicalIsNotInInvoicesWithStatus_IN_PROGRESS() {
        long userId = 2;
        long periodicalId = 23;
        assertFalse(invoiceDao.isPeriodicalInUnpaidInvoice(userId, periodicalId));

    }

}

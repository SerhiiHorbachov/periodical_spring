package ua.com.periodicals.dao.impl;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.periodicals.config.HibernateConfig;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.exception.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PeriodicalDaoImplTest {

    private static ApplicationContext applicationContext;
    private static PeriodicalDaoImpl periodicalDao;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(HibernateConfig.class);
        periodicalDao = applicationContext.getBean(PeriodicalDaoImpl.class);
        applicationContext.getBean("PopulateData");
    }

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    @Order(2)
    void findAll_ShouldReturnListOfPeriodicals() {
        List<Periodical> periodicals = periodicalDao.findAll();
        assertEquals(23, periodicals.size());
    }

    @Test
    void findPerPage_ShouldReturnListOfPeriodicalPerPage() {
        int firstResult = 4;
        int maxResults = 6;

        List<Periodical> periodicals = periodicalDao.findPerPage(firstResult, maxResults);

        assertEquals(maxResults, periodicals.size());
    }

    @Test
    @Order(1)
    void getCount_ShouldReturnTotalNumberOfPeriodicalsInDatabase() {
        long size = periodicalDao.getCount();
        assertEquals(23, size);
    }

    @Test
    @Order(3)
    void savePeriodical_ShouldReturnSavedPeriodicalWithId() {
        Periodical newPeriodical = new Periodical("Top Gear", "description", 1234);
        Periodical savedPeriodical = periodicalDao.save(newPeriodical);
        assertEquals(24, savedPeriodical.getId());
    }

    @Test
    void getById_ShouldReturnPeriodicalWithMatchingId() {
        long periodicalId = 1;
        assertNotNull(periodicalDao.getById(periodicalId));
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenIdIsNotInDatabase() {
        long invalidPeriodicalId = 0;
        assertThrows(NotFoundException.class, () -> periodicalDao.getById(invalidPeriodicalId));
    }

    @Test
    void update_ShouldUpdatePeriodical() {

        long periodicalId = 10;
        String changedName = "Changed";

        Periodical periodicalToUpdate = periodicalDao.getById(periodicalId);

        periodicalToUpdate.setName("Changed");

        Periodical updated = periodicalDao.update(periodicalToUpdate);

        assertTrue(changedName.equals(updated.getName()));
    }

    @Test
    void deleteById_ShouldRemovePeriodicalFromDatabase() {
        long periodicalIdToRemove = 23;
        long countBeforeRemoval = periodicalDao.getCount();

        periodicalDao.deleteById(periodicalIdToRemove);
        long countAfterRemoval = periodicalDao.getCount();

        assertEquals(countBeforeRemoval, countAfterRemoval + 1);
    }

    @Test
    void findAllByInvoiceId_ShouldReturnListOfPeriodicalsRelatedToInvoiceId() {
        long invoiceId = 2;
        int expectedListSize = 3;

        List<Periodical> periodicals = periodicalDao.findAllByInvoiceId(invoiceId);
        assertEquals(expectedListSize, periodicals.size());
    }

}

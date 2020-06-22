package ua.com.periodicals.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.com.periodicals.config.AppSecurityConfig;
import ua.com.periodicals.dao.PeriodicalDao;
import ua.com.periodicals.entity.Periodical;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicalServiceTest {

    private static ApplicationContext applicationContext;

    @Mock
    PeriodicalDao periodicalDao;

    @InjectMocks
    private static PeriodicalService periodicalService;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(AppSecurityConfig.class);
        periodicalService = applicationContext.getBean(PeriodicalService.class);
    }

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void getAllPeriodicals_ShouldReturnListOfPeriodicals() {

        int expectedListSize = 10;
        List<Periodical> periodicalList = new ArrayList<>();

        for (int i = 0; i < expectedListSize; i++) {
            periodicalList.add(new Periodical());
        }

        when(periodicalDao.findAll()).thenReturn(periodicalList);

        assertEquals(expectedListSize, periodicalDao.findAll().size());

    }

    @Test
    void getCount_ShouldReturnAmountOfStoredPeriodicals() {
        long expectedCount = 100;
        when(periodicalDao.getCount()).thenReturn(expectedCount);
        assertEquals(expectedCount, periodicalDao.getCount());
    }

    @Test
    void save_ShouldReturnStoredEntityWhen_NewPeriodicalsIsSaved() {
        Periodical periodicalToSave = new Periodical("New Periodical", "Some description", 200);
        Periodical storedPeriodicalMock = new Periodical(101, "New Periodical", "Some description", 200);

        when(periodicalService.save(periodicalToSave)).thenReturn(storedPeriodicalMock);

        assertEquals(storedPeriodicalMock, periodicalService.save(periodicalToSave));
    }

    @Test
    void update_ShouldReturnUpdatedEntityWhen_PeriodicalsIsUpdated() {
        Periodical periodicalToUpdate = new Periodical(101, "New Periodical", "Some description", 200);

        when(periodicalService.save(periodicalToUpdate)).thenReturn(periodicalToUpdate);

        assertEquals(periodicalToUpdate, periodicalService.save(periodicalToUpdate));
    }

    @Test
    void getById_ShouldReturnPeriodicalWithMatchingId() {
        long periodicalId = 101;
        Periodical periodicalMock = new Periodical(periodicalId, "New Periodical", "Some description", 200);

        when(periodicalService.getById(periodicalId)).thenReturn(periodicalMock);

        assertEquals(periodicalMock, periodicalService.getById(periodicalId));
    }

    @Test
    void findAllByInvoiceId_ShouldReturnListOfAllPeriodicalsRelatedToInvoiceId() {
        long invoiceId = 5;
        int expectedListSize = 10;

        List<Periodical> periodicals = new ArrayList<>();

        for (int i = 0; i < expectedListSize; i++) {
            periodicals.add(new Periodical());
        }

        when(periodicalService.findAllByInvoiceId(invoiceId)).thenReturn(periodicals);

        assertEquals(expectedListSize, periodicalService.findAllByInvoiceId(invoiceId).size());
    }

}

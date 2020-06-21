package ua.com.periodicals.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.periodicals.dao.impl.PeriodicalDaoImpl;
import ua.com.periodicals.entity.Periodical;

import static org.junit.jupiter.api.Assertions.*;

class PeriodicalServiceTest {

    @Mock
    PeriodicalDaoImpl periodicalDao;

    @InjectMocks
    @Autowired
    PeriodicalService periodicalService;


}
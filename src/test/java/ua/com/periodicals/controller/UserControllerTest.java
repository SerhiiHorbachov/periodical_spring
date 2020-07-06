package ua.com.periodicals.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.security.MyUserDetailsService;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "test")
class UserControllerTest {

    private final static int ITEMS_PER_PAGE = 10;
    private final static long TOTAL_PERIODICALS_COUNT = 100L;

    private final static String PERIODICAL_ATTR = "periodical";
    private final static String PERIODICALS_ATTR = "periodicals";
    private final static String CURRENT_PAGE_ATTR = "currentPage";
    private final static String TOTAL_PAGES_ATTR = "totalPages";
    private final static String SUBSCRIPTIONS = "subscriptions";
    private final static String INVOICES_ATTR = "invoices";
    private final static String CART_ATTR = "cart";
    private final static String USER_ATTR = "user";

    private final static String USER_MAIN_PAGE = "user/main";
    private final static String USER_SUBSCRIPTIONS_PAGE = "user/subscriptions";

    private final static String USER_SUBSCRIPTIONS_PATH = "/main/subscriptions";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    PeriodicalService periodicalService;

    @MockBean
    InvoiceService invoiceService;

    @MockBean
    MyUserDetailsService myUserDetailsService;

    @Autowired
    @InjectMocks
    UserController userController;

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void getPeriodicalsPerPage_ShouldReturn_PageWithPeriodicals() throws Exception {

        List<Periodical> periodicals = new ArrayList<>();

        when(periodicalService.getPeriodicalsPage(anyInt(), anyInt())).thenReturn(periodicals);
        when(periodicalService.getCount()).thenReturn(TOTAL_PERIODICALS_COUNT);

        mockMvc.perform(get("/main").param("page", String.valueOf(2)))
            .andExpect(status().isOk())
            .andExpect(view().name(USER_MAIN_PAGE))
            .andExpect(model().attributeExists(PERIODICALS_ATTR, CURRENT_PAGE_ATTR, TOTAL_PAGES_ATTR));

    }

    @Test
    void showActiveSubscriptions_ShouldReturn_ActiveSubscriptions() throws Exception {

        Set<Periodical> periodicals = new HashSet<>();
        User user = new User();
        user.setId(12);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setRole(User.Role.USER);
        user.setEmail("email@email.com");

        user.setSubscriptions(periodicals);

        when(myUserDetailsService.getLoggedUser()).thenReturn(user);

        mockMvc.perform(get("/main/subscriptions"))
            .andExpect(status().isOk())
            .andExpect(view().name(USER_SUBSCRIPTIONS_PAGE))
            .andExpect(model().attributeExists(SUBSCRIPTIONS));

    }

    @Test
    void unsubscribe() throws Exception {
        long periodicalId = 2;
        long userId = 1;
        User user = new User();
        user.setId(userId);
        when(myUserDetailsService.getLoggedUser()).thenReturn(user);

        mockMvc.perform(post("/main/subscriptions")
            .param("id", String.valueOf(periodicalId))
        ).andExpect(redirectedUrl(USER_SUBSCRIPTIONS_PATH));
    }

}
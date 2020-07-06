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
import ua.com.periodicals.dto.PeriodicalDto;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.exception.EntityEngagedException;
import ua.com.periodicals.service.InvoiceService;
import ua.com.periodicals.service.PeriodicalService;
import ua.com.periodicals.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "test")
class AdminControllerTest {

    private final static int ITEMS_PER_PAGE = 10;
    private final static long TOTAL_PERIODICALS_COUNT = 100L;

    private final static String PERIODICAL_ATTR = "periodical";
    private final static String PERIODICALS_ATTR = "periodicals";
    private final static String CURRENT_PAGE_ATTR = "currentPage";
    private final static String TOTAL_PAGES_ATTR = "totalPages";
    private final static String INVOICE_ATTR = "invoice";
    private final static String INVOICES_ATTR = "invoices";
    private final static String CART_ATTR = "cart";
    private final static String USER_ATTR = "user";
    private final static String PERIODICAL_IN_USE_ATTR = "periodicalInUse";

    private final static String APPROVE_CMD = "approve";
    private final static String CANCEL_CMD = "cancel";

    private final static String ADMIN_PERIODICALS_PAGE = "admin/periodicals/list-periodicals";
    private final static String ADMIN_NEW_PERIODICAL_PAGE = "admin/periodicals/new-periodical";
    private final static String ADMIN_LIST_INVOICES_PAGE = "admin/invoice/list-invoices";
    private final static String ADMIN_INVOICE_PAGE = "admin/invoice/invoice";
    private final static String ADMIN_PERIODICALS_EDIT_PAGE = "admin/periodicals/edit";

    private final static String ADMIN_PERIODICALS_PATH = "/admin/periodicals";
    private final static String ADMIN_INVOICES_VIEW_PATH = "/admin/invoices/view";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PeriodicalService periodicalService;

    @MockBean
    InvoiceService invoiceService;

    @MockBean
    UserService userService;

    @Autowired
    @InjectMocks
    AdminController adminController;

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void getPeriodicalsPerPageTest_ShouldReturnListOfPeriodicalsPerRequestedPage() throws Exception {
        List<Periodical> mockedPeriodicalsList = new ArrayList<>();

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            mockedPeriodicalsList.add(new Periodical());
        }

        when(periodicalService.getCount()).thenReturn(TOTAL_PERIODICALS_COUNT);
        when(periodicalService.getPeriodicalsPage(anyInt(), anyInt())).thenReturn(mockedPeriodicalsList);

        mockMvc.perform(get("/admin/periodicals"))
            .andExpect(status().isOk())
            .andExpect(view().name(ADMIN_PERIODICALS_PAGE))
            .andExpect(model().attributeExists(PERIODICALS_ATTR, CURRENT_PAGE_ATTR, TOTAL_PAGES_ATTR));
    }

    @Test
    void showNewPeriodicalForm_ShouldReturnNewPeriodicalsPage() throws Exception {
        mockMvc.perform(get("/admin/periodicals/new"))
            .andExpect(status().isOk())
            .andExpect(view().name(ADMIN_NEW_PERIODICAL_PAGE))
            .andExpect(model().attributeExists(PERIODICAL_ATTR));
    }

    @Test
    void savePeriodical_ShouldRedirectToPeriodicalsPage() throws Exception {

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setName("TestPeriodical");
        periodicalDto.setMonthlyPrice(13.78f);

        mockMvc.perform(post("/admin/periodicals/new").flashAttr(PERIODICAL_ATTR, periodicalDto))
            .andExpect(redirectedUrl(ADMIN_PERIODICALS_PATH));

    }

    @Test
    void savePeriodical_ShouldReturnBackToNewPeriodicalPage_WhenValidationFails() throws Exception {

        String invalidBlankName = "  ";

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setName(invalidBlankName);
        periodicalDto.setMonthlyPrice(13.78f);

        mockMvc.perform(post("/admin/periodicals/new").flashAttr(PERIODICAL_ATTR, periodicalDto))
            .andExpect(view().name(ADMIN_NEW_PERIODICAL_PAGE));

    }

    @Test
    void listUnprocessedInvoices_ShouldReturnListOfUnprocessedInvoices() throws Exception {
        ArrayList<Invoice> unprocessedInvoicesMock = new ArrayList<>();

        when(invoiceService.getUnprocessedInvoices()).thenReturn(unprocessedInvoicesMock);

        mockMvc.perform(get("/admin/invoices"))
            .andExpect(status().isOk())
            .andExpect(view().name(ADMIN_LIST_INVOICES_PAGE))
            .andExpect(model().attributeExists(INVOICES_ATTR));

    }

    @Test
    void viewInvoice_ShouldReturnInvoiceInfo() throws Exception {

        long userId = 1;
        long invoiceId = 1;

        Invoice invoiceMock = new Invoice();
        invoiceMock.setId(invoiceId);
        invoiceMock.setUserId(userId);
        invoiceMock.setStatus(Invoice.STATUS.IN_PROGRESS);

        ArrayList<Periodical> periodicalsMock = new ArrayList<>();

        User userMock = new User();
        userMock.setId(userId);

        when(invoiceService.getById(anyLong())).thenReturn(invoiceMock);
        when(periodicalService.findAllByInvoiceId(invoiceMock.getId())).thenReturn(periodicalsMock);
        when(userService.findById(invoiceMock.getUserId())).thenReturn(userMock);

        mockMvc.perform(get("/admin/invoices/view").param("id", String.valueOf(invoiceId)))
            .andExpect(status().isOk())
            .andExpect(view().name(ADMIN_INVOICE_PAGE))
            .andExpect(model().attributeExists(INVOICE_ATTR, CART_ATTR, USER_ATTR));
    }

    @Test
    void processInvoice_ShouldApproveInvoice_WhenCommandIsApprove() throws Exception {
        long invoiceId = 1;

        when(invoiceService.approveInvoice(invoiceId)).thenReturn(true);

        mockMvc.perform(post("/admin/invoices/view")
            .param("command", APPROVE_CMD)
            .param("id", String.valueOf(invoiceId))
        ).andExpect(redirectedUrl(ADMIN_INVOICES_VIEW_PATH + "?id=" + invoiceId));

    }

    @Test
    void processInvoice_ShouldCancelInvoice_WhenCommandIsCancel() throws Exception {
        long invoiceId = 1;

        when(invoiceService.approveInvoice(invoiceId)).thenReturn(true);

        mockMvc.perform(post("/admin/invoices/view")
            .param("command", CANCEL_CMD)
            .param("id", String.valueOf(invoiceId))
        ).andExpect(redirectedUrl(ADMIN_INVOICES_VIEW_PATH + "?id=" + invoiceId));

    }

    @Test
    void updatePeriodicalView() throws Exception {

        long id = 1;

        Periodical mockPeriodical = new Periodical();
        mockPeriodical.setId(id);
        mockPeriodical.setName("Test Periodicals");
        mockPeriodical.setMonthlyPrice(1212);

        when(periodicalService.getById(id)).thenReturn(mockPeriodical);

        mockMvc.perform(get("/admin/periodicals/edit").param("id", String.valueOf(id)))
            .andExpect(status().isOk())
            .andExpect(view().name(ADMIN_PERIODICALS_EDIT_PAGE))
            .andExpect(model().attributeExists(PERIODICAL_ATTR));
    }

    @Test
    void updatePeriodical_Should_RedirectToPeriodicalList() throws Exception {
        String name = "Test Periodical";
        long periodicalId = 1;

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setName(name);
        periodicalDto.setMonthlyPrice(13.78f);

        Periodical periodicalToUpdate = new Periodical();
        periodicalToUpdate.setId(periodicalId);
        periodicalToUpdate.setName(periodicalDto.getName());
        periodicalToUpdate.setDescription(periodicalDto.getDescription());
        periodicalToUpdate.setMonthlyPrice(1378);


        when(periodicalService.getById(anyLong())).thenReturn(periodicalToUpdate);

        mockMvc.perform(post("/admin/periodicals/edit").flashAttr(PERIODICAL_ATTR, periodicalDto))
            .andExpect(redirectedUrl(ADMIN_PERIODICALS_PATH));
    }

    @Test
    void updatePeriodical_ShouldReturnBackToPeriodicalEditPage_WhenValidationFails() throws Exception {

        String invalidBlankName = "  ";

        PeriodicalDto periodicalDto = new PeriodicalDto();
        periodicalDto.setName(invalidBlankName);
        periodicalDto.setMonthlyPrice(13.78f);

        mockMvc.perform(post("/admin/periodicals/edit").flashAttr(PERIODICAL_ATTR, periodicalDto))
            .andExpect(view().name(ADMIN_PERIODICALS_EDIT_PAGE));

    }

    @Test
    void deletePeriodical_shouldRedirectToPeriodicalsPage_WhenDeletedSuccessfully() throws Exception {
        long id = 1;
        int currentPage = 2;
        int totalPages = 10;

        mockMvc.perform(post("/admin/periodicals/delete")
            .param("id", String.valueOf(id))
            .param("currentPage", String.valueOf(currentPage))
            .param("totalPages", String.valueOf(totalPages)))
            .andExpect(redirectedUrl(ADMIN_PERIODICALS_PATH));
    }

    @Test
    void deletePeriodical_shouldSendBackToPeriodicalPage_WhenEntityEngagedExceptionIsThrown() throws Exception {
        long id = 1;

        int currentPage = 2;
        int totalPages = 10;

        doThrow(EntityEngagedException.class).when(periodicalService).deleteById(anyLong());

        mockMvc.perform(post("/admin/periodicals/delete")
            .param("id", String.valueOf(id))
            .param("currentPage", String.valueOf(currentPage))
            .param("totalPages", String.valueOf(totalPages)))
            .andExpect(view().name(ADMIN_PERIODICALS_PAGE))
            .andExpect(model().attributeExists(
                CURRENT_PAGE_ATTR,
                TOTAL_PAGES_ATTR,
                PERIODICALS_ATTR,
                PERIODICAL_IN_USE_ATTR)
            );
    }

}

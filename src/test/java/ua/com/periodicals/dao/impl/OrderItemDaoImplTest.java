package ua.com.periodicals.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.com.periodicals.config.HibernateConfig;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.entity.OrderItem;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDaoImplTest {

    private static ApplicationContext applicationContext;
    private static OrderItemDao orderItemDao;

    @BeforeAll
    public static void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(HibernateConfig.class);
        orderItemDao = applicationContext.getBean(OrderItemDao.class);
        applicationContext.getBean("PopulateData");
    }

    @Test
    void save_ShouldStoreOrderItemInDatabase(){
        long invoiceId = 11;
        long periodicalId = 20;
        long costPerMonth = 1000;
        long expectedOrderItemId = 18;

        OrderItem item = new OrderItem(invoiceId, periodicalId, costPerMonth);

        OrderItem storedItem = orderItemDao.save(item);

        assertEquals(expectedOrderItemId, storedItem.getId());

    }

}
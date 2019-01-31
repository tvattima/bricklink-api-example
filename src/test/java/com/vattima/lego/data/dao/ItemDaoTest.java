package com.vattima.lego.data.dao;

import com.vattima.lego.data.dto.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class ItemDaoTest {

    @Autowired
    private ItemDao itemDao;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Sql({"data.sql"})
    public void findItemById() {
        Item item = new Item();
        item.setItemNumber("1234");
        item.setItemName("Test Item");
        itemDao.insertItem(item);
    }

    @TestConfiguration
    @ComponentScan(basePackages = {"com.vattima"})
    static class DaoConfiguration {
    }
}
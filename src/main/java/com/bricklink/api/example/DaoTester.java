package com.bricklink.api.example;

import com.vattima.lego.data.dao.BricklinkInventoryDao;
import com.vattima.lego.data.dao.InventoryIndexDao;
import com.vattima.lego.data.dao.ItemDao;
import com.vattima.lego.data.dao.TransactionDao;
import com.vattima.lego.data.dto.BricklinkInventoryWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
public class DaoTester {
    public static void main(String[] args) {
        SpringApplication.run(BricklinkTestMain.class, args);
    }

    //@Component
    @RequiredArgsConstructor
    public static class DaoRunner implements CommandLineRunner {

        @Autowired
        private ItemDao itemDao;

        @Autowired
        private TransactionDao transactionDao;

        @Autowired
        private InventoryIndexDao inventoryIndexDao;

        @Autowired
        private BricklinkInventoryDao bricklinkInventoryDao;

        @Override
        public void run(String... args) throws Exception {
//            Item item = new Item();
//            item.setItemNumber("X9999");
//            item.setItemName("X 9999");
//            item.setNumberOfPieces(999);
//            item.setIssueYear(1999);
//            item.setIssueLocation("U");
//            item.setThemeId(11);
//            item.setItemTypeCode("S");
//            item.setNotes("DELETE ME!!");
//            itemDao.insertItem(item);
//            log.info("New Item [{}]", item);

//            List<Transaction> transactions = transactionDao.findByTransactionDate(LocalDate.of(1998, 6, 13));
//            for (Transaction transaction : transactions) {
//                log.info("[{}]", transaction);
//            }

//            InventoryIndex inventoryIndex = new InventoryIndex();
//            inventoryIndex.setBoxIndex(2);
//            inventoryIndex.setBoxNumber("Box 27");
//            inventoryIndex.setItemNumber("8533");
//            inventoryIndex.setSealed("false");
//            inventoryIndex.setQuantity(0);
//            inventoryIndex.setDescription("ZZZZZZ Is this Bricklink 8533-1 - Gali OR 8533-2 - Gali - With mini CD-ROM?");
//            inventoryIndexDao.udpateInventoryIndex(inventoryIndex);

            List<BricklinkInventoryWork> bricklinkInventoryWorkList = bricklinkInventoryDao.getAllInventoryWork();
            for (BricklinkInventoryWork bricklinkInventoryWork : bricklinkInventoryWorkList) {
                log.info("{}", bricklinkInventoryWork);
            }

        }
    }
}

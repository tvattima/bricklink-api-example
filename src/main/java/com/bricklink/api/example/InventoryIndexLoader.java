package com.bricklink.api.example;

import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import com.vattima.lego.sheet.meta.BooleanCellDescriptor;
import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.meta.IntegerCellDescriptor;
import com.vattima.lego.sheet.meta.StringCellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;
import com.vattima.lego.sheet.service.LegoItemSheetService;
import net.bricklink.data.lego.dao.InventoryIndexDao;
import net.bricklink.data.lego.dto.InventoryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableConfigurationProperties
public class InventoryIndexLoader {
    private static Logger logger = LoggerFactory.getLogger(BricklinkTestMain.class);

    public static void main(String[] args) {
        SpringApplication.run(InventoryIndexLoader.class, args);
    }

    //@Component
    public static class Loader implements CommandLineRunner {
        @Autowired
        LegoItemSheetProperties legoItemSheetProperties;

        @Autowired
        LegoItemSheetService legoItemSheetService;

        @Autowired
        private InventoryIndexDao inventoryIndexDao;

        @Override
        public void run(String... args) throws Exception {
            List<CellDescriptor<?>> descriptors = Arrays.asList(
                    new IntegerCellDescriptor(),
                    new StringCellDescriptor(),
                    new StringCellDescriptor(),
                    new BooleanCellDescriptor(),
                    new IntegerCellDescriptor(),
                    new StringCellDescriptor());
            List<LegoSheetItem> legoSheetItems = legoItemSheetService.getLegoItems(descriptors);
            for (LegoSheetItem legoSheetItem : legoSheetItems) {
                InventoryIndex inventoryIndex = new InventoryIndex();
                inventoryIndex.setBoxIndex(legoSheetItem.getRow());
                inventoryIndex.setBoxNumber(legoSheetItem.getBox());
                inventoryIndex.setItemNumber(legoSheetItem.getItemNumber());
                inventoryIndex.setSealed(Boolean.toString(legoSheetItem.isSealed()));
                inventoryIndex.setQuantity(legoSheetItem.getQuantity());
                inventoryIndex.setDescription(legoSheetItem.getDescription());
                inventoryIndexDao.insertInventoryIndex(inventoryIndex);
            }
        }
    }

}

package com.vattima.lego.sheet.service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.vattima.lego.sheet.api.LegoItemSheetApi;
import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import com.vattima.lego.sheet.meta.BooleanCellDescriptor;
import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.meta.IntegerCellDescriptor;
import com.vattima.lego.sheet.meta.StringCellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LegoItemSheetService implements LegoItemSheetApi {

    private final LegoItemSheetProperties legoItemSheetProperties;
    private final GoogleSheetsService googleSheetsService;

    @Override
    public List<LegoSheetItem> getLegoItems(List<? extends CellDescriptor<?>> descriptors) {
        List<List<Object>> emptyValues = new ArrayList<>();
        List<LegoSheetItem> legoSheetItems = new ArrayList<>();

        String range = legoItemSheetProperties.getWorkbook() + "!" + legoItemSheetProperties.getRange();
        ValueRange response = null;
        try {
            response = googleSheetsService.getSheetsService()
                                          .spreadsheets()
                                          .values()
                                          .get(legoItemSheetProperties.getId(), range)
                                          .execute();
        } catch (IOException e) {
            throw new LegoItemSheetSheetServiceException(e);
        }

        List<List<Object>> values = Optional.ofNullable(response.getValues())
                                            .orElse(emptyValues);
        for (List row : values) {
            LegoSheetItem legoSheetItem = LegoSheetItem.builder()
                                                       .build();

            int col = 0;
            for (CellDescriptor cd : descriptors) {
                if (col < row.size()) {
                    cd.setValue(row.get(col));
                    switch (col) {
                        case 0:
                            legoSheetItem.setRow(((IntegerCellDescriptor)cd).getValue(0));
                            break;
                        case 1:
                            legoSheetItem.setBox(((StringCellDescriptor)cd).getValue());
                            break;
                        case 2:
                            legoSheetItem.setItemNumber(((StringCellDescriptor)cd).getValue());
                            break;
                        case 3:
                            legoSheetItem.setSealed(((BooleanCellDescriptor)cd).getValue());
                            break;
                        case 4:
                            legoSheetItem.setQuantity(((IntegerCellDescriptor)cd).getValue(1));
                            break;
                        case 5:
                            legoSheetItem.setDescription(((StringCellDescriptor)cd).getValue());
                            break;
                    }
                }
                col++;
            }
            legoSheetItems.add(legoSheetItem);

        }
        return legoSheetItems;
    }
}

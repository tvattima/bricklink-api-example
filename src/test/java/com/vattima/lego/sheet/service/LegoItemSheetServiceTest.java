package com.vattima.lego.sheet.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Get;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import com.vattima.lego.sheet.meta.BooleanCellDescriptor;
import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.meta.IntegerCellDescriptor;
import com.vattima.lego.sheet.meta.StringCellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LegoItemSheetServiceTest {
    private LegoItemSheetProperties legoItemSheetProperties;
    private GoogleSheetsService googleSheetsService;
    private ValueRange valueRange;

    @Before
    public void setup() {
        legoItemSheetProperties = new LegoItemSheetProperties();

        legoItemSheetProperties.setId("1");
        legoItemSheetProperties.setWorkbook("workbook");
        legoItemSheetProperties.setRange("A2:F");

        googleSheetsService = mock(GoogleSheetsService.class);
        Sheets sheets = mock(Sheets.class);
        Spreadsheets spreadsheets = mock(Spreadsheets.class);
        Values values = mock(Values.class);
        Get get = mock(Get.class);
        valueRange = new ValueRange();

        when(googleSheetsService.getSheetsService()).thenReturn(sheets);
        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        try {
            when(values.get("1", "workbook!A2:F")).thenReturn(get);
            when(get.execute()).thenReturn(valueRange);
        } catch (IOException e) {
            throw new LegoItemSheetSheetServiceException(e);
        }
    }

    @Test
    public void getLegoItems_returnsNonEmptyList() {
        valueRange.setValues(
                Arrays.asList(
                        Arrays.asList(1, "Box 1", "1234", null, 1, null),
                        Arrays.asList(2, "Box 2", "567", "x", null, "description"),
                        Arrays.asList(3, "Box 3", "89012", null, null, null)
                ));

        LegoItemSheetService legoItemSheetService = new LegoItemSheetService(legoItemSheetProperties, googleSheetsService);
        List<CellDescriptor<?>> descriptors = Arrays.asList(
                new IntegerCellDescriptor(),
                new StringCellDescriptor(),
                new StringCellDescriptor(),
                new BooleanCellDescriptor(),
                new IntegerCellDescriptor(),
                new StringCellDescriptor());
        List<LegoSheetItem> legoSheetItems = legoItemSheetService.getLegoItems(descriptors);
        assertThat(legoSheetItems)
                .isNotNull()
                .hasSize(3)
                .hasOnlyElementsOfType(LegoSheetItem.class);

        assertThat(legoSheetItems.get(0).getRow()).isEqualTo(1);
        assertThat(legoSheetItems.get(0).getBox()).isEqualTo("Box 1");
        assertThat(legoSheetItems.get(0).getItemNumber()).isEqualTo("1234");
        assertThat(legoSheetItems.get(0).isSealed()).isFalse();
        assertThat(legoSheetItems.get(0).getQuantity()).isEqualTo(1);
        assertThat(legoSheetItems.get(0).getDescription()).isEqualTo("");

        assertThat(legoSheetItems.get(1).getRow()).isEqualTo(2);
        assertThat(legoSheetItems.get(1).getBox()).isEqualTo("Box 2");
        assertThat(legoSheetItems.get(1).getItemNumber()).isEqualTo("567");
        assertThat(legoSheetItems.get(1).isSealed()).isTrue();
        assertThat(legoSheetItems.get(1).getQuantity()).isEqualTo(1);
        assertThat(legoSheetItems.get(1).getDescription()).isEqualTo("description");

        
        assertThat(legoSheetItems.get(2).getRow()).isEqualTo(3);
        assertThat(legoSheetItems.get(2).getBox()).isEqualTo("Box 3");
        assertThat(legoSheetItems.get(2).getItemNumber()).isEqualTo("89012");
        assertThat(legoSheetItems.get(2).isSealed()).isFalse();
        assertThat(legoSheetItems.get(2).getQuantity()).isEqualTo(1);
        assertThat(legoSheetItems.get(2).getDescription()).isEqualTo("");
    }
}
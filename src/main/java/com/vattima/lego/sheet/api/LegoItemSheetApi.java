package com.vattima.lego.sheet.api;

import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;

import java.util.List;

public interface LegoItemSheetApi {
    List<LegoSheetItem> getLegoItems(List<? extends CellDescriptor<?>> descriptors);
}

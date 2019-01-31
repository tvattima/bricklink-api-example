package com.vattima.lego.sheet.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LegoSheetItem {
    private int row;
    private String box;
    private String itemNumber;
    private boolean sealed;
    private int quantity;
    private String description;

    @Override
    public String toString() {
        return "LegoSheetItem{" +
                "row=" + row +
                ", box='" + box + '\'' +
                ", itemNumber='" + itemNumber + '\'' +
                ", sealed=" + sealed +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                '}';
    }
}

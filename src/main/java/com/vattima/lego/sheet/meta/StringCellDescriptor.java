package com.vattima.lego.sheet.meta;

import java.util.Optional;

public class StringCellDescriptor implements CellDescriptor<String> {
    private String value;

    @Override
    public void setValue(Object v) {
        value = Optional
                .ofNullable(v)
                .map(Object::toString)
                .map(String::trim)
                .orElse("");
    }

    @Override
    public String getValue() {
        return value;
    }
}

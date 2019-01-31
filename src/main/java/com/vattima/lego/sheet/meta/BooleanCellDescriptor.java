package com.vattima.lego.sheet.meta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BooleanCellDescriptor implements CellDescriptor<Boolean> {
    private final List<String> TRUE_VALUES = Arrays.asList("x", "y", "X", "Y");
    private Boolean value;

    @Override
    public void setValue(Object v) {
        value = Optional
                .ofNullable(v)
                .map(Object::toString)
                .map(String::trim)
                .map(TRUE_VALUES::contains)
                .orElse(false);
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}

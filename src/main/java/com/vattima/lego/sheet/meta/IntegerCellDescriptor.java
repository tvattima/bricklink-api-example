package com.vattima.lego.sheet.meta;

import java.util.Optional;

public class IntegerCellDescriptor implements CellDescriptor<Optional<Integer>> {
    private Integer value;

    @Override
    public void setValue(Object v) {
        value = Optional
                .ofNullable(v)
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(Optional
                        .ofNullable(v)
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(String::trim)
                        .map(this::valueOf)
                        .orElse(null));
    }

    private Integer valueOf(String stringValue) {
        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Optional<Integer> getValue() {
        return Optional.ofNullable(value);
    }

    public Integer getValue(Integer defaultValue) {
        return Optional.ofNullable(value).orElse(defaultValue);
    }
}

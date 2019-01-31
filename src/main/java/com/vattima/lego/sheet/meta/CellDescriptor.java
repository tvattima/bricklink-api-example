package com.vattima.lego.sheet.meta;

public interface CellDescriptor<T> {
    void setValue(Object v);
    T getValue();
}

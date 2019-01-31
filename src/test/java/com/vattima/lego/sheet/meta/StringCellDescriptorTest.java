package com.vattima.lego.sheet.meta;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCellDescriptorTest {

    @Test
    public void test_setNonNullValue_convertsToString() {
        StringCellDescriptor stringCellDescriptor = new StringCellDescriptor();

        stringCellDescriptor.setValue("");
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("").isEmpty();

        stringCellDescriptor.setValue("A");
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("A");

        stringCellDescriptor.setValue(123);
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("123");

        stringCellDescriptor.setValue("123.45");
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("123.45");

        stringCellDescriptor.setValue("   abc   ");
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("abc");

        stringCellDescriptor.setValue('x');
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("x");

        Date expectedDate = new Date();
        stringCellDescriptor.setValue(expectedDate);
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo(expectedDate.toString());
    }

    @Test
    public void test_setNullValue_convertsToEmptyString() {
        StringCellDescriptor stringCellDescriptor = new StringCellDescriptor();

        String nullString = null;
        stringCellDescriptor.setValue(nullString);
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("").isEmpty();

        Integer nullInteger = null;
        stringCellDescriptor.setValue(nullInteger);
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("").isEmpty();

        Date nullDate = null;
        stringCellDescriptor.setValue(nullDate);
        assertThat(stringCellDescriptor.getValue()).isNotNull();
        assertThat(stringCellDescriptor.getValue()).isEqualTo("").isEmpty();
    }
}
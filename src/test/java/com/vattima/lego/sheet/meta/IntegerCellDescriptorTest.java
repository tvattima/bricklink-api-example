package com.vattima.lego.sheet.meta;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IntegerCellDescriptorTest {

    @Test
    public void test_setAsValidInteger_returnsInteger() {
        IntegerCellDescriptor integerCellDescriptor = new IntegerCellDescriptor();

        integerCellDescriptor.setValue(123);
        Optional<Integer> actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(123);

        integerCellDescriptor.setValue(123678);
        actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(123678);
    }

    @Test
    public void test_setAsValidIntegerString_returnsInteger() {
        IntegerCellDescriptor integerCellDescriptor = new IntegerCellDescriptor();

        integerCellDescriptor.setValue("123");
        Optional<Integer> actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(123);

        integerCellDescriptor.setValue(" 1   ");
        actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(1);

        integerCellDescriptor.setValue(" 567   ");
        actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(567);

        integerCellDescriptor.setValue("123678");
        actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isTrue();
        assertThat(actualValue.get()).isEqualTo(123678);
    }

    @Test
    public void test_setAsNull_returnsNull() {
        IntegerCellDescriptor integerCellDescriptor = new IntegerCellDescriptor();

        Integer expectedValue = null;
        integerCellDescriptor.setValue(expectedValue);
        Optional<Integer> actualValue = integerCellDescriptor.getValue();
        assertThat(actualValue).isNotNull().isInstanceOf(Optional.class);
        assertThat(actualValue.isPresent()).isFalse();
        assertThatThrownBy(actualValue::get).isInstanceOf(NoSuchElementException.class);
    }
}
package com.lab4.calculator.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lab4.calculator.domain.model.value.WeightValue;
import org.junit.jupiter.api.Test;

class WeightValueTest {

    @Test
    void shouldAcceptFractionalWeight() {
        WeightValue weightValue = new WeightValue(0.5);
        assertEquals(0.5, weightValue.getValue());
    }

    @Test
    void shouldRejectInvalidWeight() {
        assertThrows(IllegalArgumentException.class, () -> new WeightValue(0.0));
        assertThrows(IllegalArgumentException.class, () -> new WeightValue(1.5));
    }
}

package com.lab4.calculator.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lab4.calculator.domain.model.value.GradeValue;
import org.junit.jupiter.api.Test;

class GradeValueTest {

    @Test
    void shouldAcceptValidGrade() {
        GradeValue gradeValue = new GradeValue(8.5);
        assertEquals(8.5, gradeValue.getValue());
    }

    @Test
    void shouldAllowNullGrade() {
        GradeValue gradeValue = new GradeValue(null);
        assertNull(gradeValue.getValue());
    }

    @Test
    void shouldDetectGradePresence() {
        GradeValue gradeValue = new GradeValue(5.0);
        assertEquals(false, gradeValue.isMissing());
    }

    @Test
    void shouldDetectMissingGrade() {
        GradeValue gradeValue = new GradeValue(null);
        assertEquals(true, gradeValue.isMissing());
    }

    @Test
    void shouldRejectInvalidGrade() {
        assertThrows(IllegalArgumentException.class, () -> new GradeValue(11.0));
        assertThrows(IllegalArgumentException.class, () -> new GradeValue(-1.0));
    }
}

package com.lab4.calculator.domain.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GradeValue {

    @Column(name = "grade_value")
    private Double value;

    public GradeValue(Double value) {
        this.value = validate(value);
    }

    private Double validate(Double value) {
        if (value == null) {
            return null;
        }
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10");
        }
        return value;
    }

    public boolean isMissing() {
        return value == null;
    }
}

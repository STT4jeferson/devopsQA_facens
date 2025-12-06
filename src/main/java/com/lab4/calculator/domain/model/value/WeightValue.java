package com.lab4.calculator.domain.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightValue {

    @Column(name = "weight_value")
    private Double value;

    public WeightValue(Double value) {
        this.value = validate(value);
    }

    private Double validate(Double value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero");
        }
        if (value > 1) {
            throw new IllegalArgumentException("Use peso fracionado (0 a 1) para as atividades");
        }
        return value;
    }
}

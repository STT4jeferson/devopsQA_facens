package com.lab4.calculator.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationActivityResponse {
    private Long id;
    private String name;
    private Double weight;
    private Double providedGrade;
    private Double requiredGrade;
}

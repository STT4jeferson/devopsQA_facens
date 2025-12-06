package com.lab4.calculator.application.dto;

import com.lab4.calculator.domain.model.SimulationStatus;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationResponse {
    private Double targetAverage;
    private Double requiredGrade;
    private SimulationStatus status;
    private List<SimulationActivityResponse> activities;
}

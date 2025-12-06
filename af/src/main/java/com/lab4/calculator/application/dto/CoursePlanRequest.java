package com.lab4.calculator.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class CoursePlanRequest {

    @Size(max = 120)
    private String student;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double targetAverage = 7.0;

    @Valid
    @NotEmpty
    private List<AssessmentRequest> assessments;
}

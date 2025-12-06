package com.lab4.calculator.application.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoursePlanResponse {
    private Long id;
    private String student;
    private Double targetAverage;
    private List<AssessmentResponse> assessments;
}

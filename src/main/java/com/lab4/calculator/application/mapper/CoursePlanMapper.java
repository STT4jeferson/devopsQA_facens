package com.lab4.calculator.application.mapper;

import com.lab4.calculator.application.dto.AssessmentResponse;

import com.lab4.calculator.application.dto.CoursePlanRequest;

import com.lab4.calculator.application.dto.CoursePlanResponse;

import com.lab4.calculator.domain.model.Assessment;

import com.lab4.calculator.domain.model.CoursePlan;

import com.lab4.calculator.domain.model.value.GradeValue;

import com.lab4.calculator.domain.model.value.WeightValue;

import java.util.List;

import java.util.stream.Collectors;

public final class CoursePlanMapper {

    private CoursePlanMapper() {
    }

    public static CoursePlan toEntity(CoursePlanRequest request) {
        CoursePlan coursePlan = CoursePlan.builder()
                .student(request.getStudent())
                .targetAverage(request.getTargetAverage())
                .build();

        request.getAssessments().forEach(assessmentRequest -> {
            Assessment assessment = Assessment.builder()
                    .name(assessmentRequest.getName())
                    .weight(new WeightValue(assessmentRequest.getWeight()))
                    .grade(assessmentRequest.getGrade() == null ? null : new GradeValue(assessmentRequest.getGrade()))
                    .build();
            coursePlan.addAssessment(assessment);
        });

        return coursePlan;
    }

    public static CoursePlanResponse toResponse(CoursePlan coursePlan) {
        List<AssessmentResponse> assessments = coursePlan.getAssessments().stream()
                .map(assessment -> AssessmentResponse.builder()
                        .id(assessment.getId())
                        .name(assessment.getName())
                        .weight(assessment.getWeight().getValue())
                        .grade(assessment.getGrade() != null ? assessment.getGrade().getValue() : null)
                        .build())
                .collect(Collectors.toList());

        return CoursePlanResponse.builder()
                .id(coursePlan.getId())
                .student(coursePlan.getStudent())
                .targetAverage(coursePlan.getTargetAverage())
                .assessments(assessments)
                .build();
    }
}

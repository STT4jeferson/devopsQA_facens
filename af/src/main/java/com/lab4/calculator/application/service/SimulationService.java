package com.lab4.calculator.application.service;

import com.lab4.calculator.application.dto.SimulationGradeRequest;
import com.lab4.calculator.application.dto.SimulationRequest;
import com.lab4.calculator.application.dto.SimulationResponse;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.model.value.WeightValue;
import com.lab4.calculator.domain.repository.CoursePlanRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final CoursePlanRepository coursePlanRepository;
    private final CalculationService calculationService;

    public SimulationResponse simulate(Long coursePlanId, SimulationRequest request) {
        CoursePlan persisted = coursePlanRepository.findById(coursePlanId)
                .orElseThrow(() -> new CoursePlanNotFoundException(coursePlanId));

        CoursePlan workingCopy = cloneForSimulation(persisted);
        applyOverrides(workingCopy, request);

        return calculationService.simulate(workingCopy);
    }

    @Transactional
    public CoursePlan persistSimulation(Long coursePlanId, SimulationRequest request) {
        CoursePlan coursePlan = coursePlanRepository.findById(coursePlanId)
                .orElseThrow(() -> new CoursePlanNotFoundException(coursePlanId));

        applyOverrides(coursePlan, request);
        return coursePlanRepository.save(coursePlan);
    }

    private CoursePlan cloneForSimulation(CoursePlan source) {
        CoursePlan copy = CoursePlan.builder()
                .id(source.getId())
                .student(source.getStudent())
                .targetAverage(source.getTargetAverage())
                .build();

        List<Assessment> clonedAssessments = new ArrayList<>();
        for (Assessment assessment : source.getAssessments()) {
            Assessment clone = Assessment.builder()
                    .id(assessment.getId())
                    .name(assessment.getName())
                    .weight(new WeightValue(assessment.getWeight().getValue()))
                    .grade(assessment.getGrade() == null ? null : new GradeValue(assessment.getGrade().getValue()))
                    .coursePlan(copy)
                    .build();
            clonedAssessments.add(clone);
        }
        copy.setAssessments(clonedAssessments);
        return copy;
    }

    private void applyOverrides(CoursePlan coursePlan, SimulationRequest request) {
        if (request == null || request.getGrades() == null) {
            return;
        }

        Map<Long, Double> newGrades = new HashMap<>();
        for (SimulationGradeRequest gradeRequest : request.getGrades()) {
            newGrades.put(gradeRequest.getAssessmentId(), gradeRequest.getGrade());
        }

        coursePlan.getAssessments().forEach(assessment -> {
            Double updatedGrade = newGrades.get(assessment.getId());
            if (updatedGrade != null) {
                assessment.setGrade(new GradeValue(updatedGrade));
            }
        });
    }
}

package com.lab4.calculator.application.service;

import com.lab4.calculator.application.dto.SimulationActivityResponse;
import com.lab4.calculator.application.dto.SimulationResponse;
import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.SimulationStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {

    public SimulationResponse simulate(CoursePlan coursePlan) {
        if (coursePlan.getAssessments().isEmpty()) {
            throw new IllegalArgumentException("O plano precisa ter ao menos uma atividade");
        }

        double totalWeight = coursePlan.getTotalWeight();
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("A soma dos pesos deve ser maior que zero");
        }

        double knownScore = coursePlan.getAssessments().stream()
                .filter(Assessment::hasGrade)
                .mapToDouble(a -> a.getGrade().getValue() * a.getWeight().getValue())
                .sum();

        double knownWeight = coursePlan.getAssessments().stream()
                .filter(Assessment::hasGrade)
                .mapToDouble(a -> a.getWeight().getValue())
                .sum();

        double remainingWeight = totalWeight - knownWeight;
        double targetAverage = coursePlan.getTargetAverage();
        double targetScore = targetAverage * totalWeight;

        double requiredGrade = remainingWeight <= 0
                ? 0
                : (targetScore - knownScore) / remainingWeight;

        double normalizedRequired = Math.min(10, Math.max(0, requiredGrade));
        SimulationStatus status = requiredGrade <= 10 && (remainingWeight > 0 || knownScore >= targetScore)
                ? SimulationStatus.ALCANCAVEL
                : SimulationStatus.INVIAVEL;

        List<SimulationActivityResponse> activities = coursePlan.getAssessments().stream()
                .map(assessment -> buildActivityResponse(assessment, normalizedRequired))
                .collect(Collectors.toList());

        return SimulationResponse.builder()
                .targetAverage(targetAverage)
                .requiredGrade(requiredGrade)
                .status(status)
                .activities(activities)
                .build();
    }

    private SimulationActivityResponse buildActivityResponse(Assessment assessment, double normalizedRequired) {
        Double required = assessment.hasGrade() ? null : normalizedRequired;
        return SimulationActivityResponse.builder()
                .id(assessment.getId())
                .name(assessment.getName())
                .weight(assessment.getWeight().getValue())
                .providedGrade(assessment.hasGrade() ? assessment.getGrade().getValue() : null)
                .requiredGrade(required)
                .build();
    }
}

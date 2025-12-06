package com.lab4.calculator.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.SimulationStatus;
import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.model.value.WeightValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class CalculationServiceTest {

    private final CalculationService calculationService = new CalculationService();

    @Test
    void shouldCalculateRequiredGradeWhenMissingScores() {
        CoursePlan coursePlan = buildPlan();
        coursePlan.getAssessments().get(0).setGrade(new GradeValue(8.0));

        var response = calculationService.simulate(coursePlan);

        assertEquals(SimulationStatus.ALCANCAVEL, response.getStatus());
        assertEquals(6.5714, response.getRequiredGrade(), 0.01);
        assertNotNull(response.getActivities());
    }

    @Test
    void shouldMarkAsInviavelWhenGradeAboveLimit() {
        Assessment prova = Assessment.builder().name("Prova").weight(new WeightValue(0.9)).grade(new GradeValue(0.0)).build();
        Assessment recuperacao = Assessment.builder().name("Recuperacao").weight(new WeightValue(0.1)).build();
        CoursePlan coursePlan = CoursePlan.builder().targetAverage(7.0).assessments(List.of(prova, recuperacao)).build();
        prova.setCoursePlan(coursePlan);
        recuperacao.setCoursePlan(coursePlan);

        var response = calculationService.simulate(coursePlan);

        assertEquals(SimulationStatus.INVIAVEL, response.getStatus());
        // required grade must exceed 10 to be inviavel
        assertEquals(70.0, response.getRequiredGrade(), 0.1);
    }

    @Test
    void shouldFailWhenNoAssessments() {
        CoursePlan coursePlan = CoursePlan.builder().targetAverage(7.0).build();
        assertThrows(IllegalArgumentException.class, () -> calculationService.simulate(coursePlan));
    }

    @Test
    void shouldReturnZeroRequiredWhenAlreadyAboveTarget() {
        Assessment prova = Assessment.builder().name("Prova").weight(new WeightValue(1.0)).grade(new GradeValue(9.0)).build();
        CoursePlan coursePlan = CoursePlan.builder().targetAverage(7.0).assessments(List.of(prova)).build();
        prova.setCoursePlan(coursePlan);

        var response = calculationService.simulate(coursePlan);

        assertEquals(SimulationStatus.ALCANCAVEL, response.getStatus());
        assertEquals(0, response.getRequiredGrade());
    }

    @Test
    void shouldBeInviavelWhenRemainingWeightIsZeroAndAverageLow() {
        Assessment prova = Assessment.builder().name("Prova").weight(new WeightValue(1.0)).grade(new GradeValue(5.0)).build();
        CoursePlan coursePlan = CoursePlan.builder().targetAverage(7.0).assessments(List.of(prova)).build();
        prova.setCoursePlan(coursePlan);

        var response = calculationService.simulate(coursePlan);

        assertEquals(SimulationStatus.INVIAVEL, response.getStatus());
    }

    @Test
    void shouldFailWhenTotalWeightIsZero() {
        CoursePlan coursePlan = mock(CoursePlan.class);
        when(coursePlan.getAssessments()).thenReturn(List.of(mock(Assessment.class)));
        when(coursePlan.getTotalWeight()).thenReturn(0.0);
        assertThrows(IllegalArgumentException.class, () -> calculationService.simulate(coursePlan));
    }

    private CoursePlan buildPlan() {
        Assessment a1 = Assessment.builder().name("P1").weight(new WeightValue(0.3)).build();
        Assessment a2 = Assessment.builder().name("P2").weight(new WeightValue(0.3)).build();
        Assessment a3 = Assessment.builder().name("Projeto").weight(new WeightValue(0.4)).build();

        CoursePlan coursePlan = CoursePlan.builder().targetAverage(7.0).build();
        coursePlan.setAssessments(List.of(a1, a2, a3));
        a1.setCoursePlan(coursePlan);
        a2.setCoursePlan(coursePlan);
        a3.setCoursePlan(coursePlan);
        return coursePlan;
    }
}

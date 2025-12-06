package com.lab4.calculator.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lab4.calculator.application.dto.SimulationGradeRequest;
import com.lab4.calculator.application.dto.SimulationRequest;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.model.value.WeightValue;
import com.lab4.calculator.domain.repository.CoursePlanRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private CoursePlanRepository repository;

    private CalculationService calculationService;
    private SimulationService simulationService;
    private CoursePlan coursePlan;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService();
        simulationService = new SimulationService(repository, calculationService);

        Assessment prova = Assessment.builder().id(1L).name("Prova").weight(new WeightValue(0.5)).build();
        Assessment projeto = Assessment.builder().id(2L).name("Projeto").weight(new WeightValue(0.5)).build();
        coursePlan = CoursePlan.builder().id(10L).student("Aluno").targetAverage(7.0).assessments(List.of(prova, projeto)).build();
        prova.setCoursePlan(coursePlan);
        projeto.setCoursePlan(coursePlan);
    }

    @Test
    void shouldApplyOverridesOnSimulationCopy() {
        when(repository.findById(10L)).thenReturn(Optional.of(coursePlan));

        SimulationGradeRequest override = new SimulationGradeRequest();
        override.setAssessmentId(1L);
        override.setGrade(9.0);
        SimulationRequest request = new SimulationRequest();
        request.setGrades(List.of(override));

        var response = simulationService.simulate(10L, request);

        assertNotNull(response);
        assertEquals(9.0, response.getActivities().stream()
                .filter(a -> a.getId().equals(1L))
                .findFirst()
                .orElseThrow()
                .getProvidedGrade());
    }

    @Test
    void shouldPersistSimulationWhenRequested() {
        when(repository.findById(10L)).thenReturn(Optional.of(coursePlan));
        when(repository.save(any(CoursePlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SimulationGradeRequest override = new SimulationGradeRequest();
        override.setAssessmentId(2L);
        override.setGrade(7.5);
        SimulationRequest request = new SimulationRequest();
        request.setGrades(List.of(override));

        CoursePlan saved = simulationService.persistSimulation(10L, request);

        assertEquals(7.5, saved.getAssessments().get(1).getGrade().getValue());
    }

    @Test
    void shouldFailWhenPlanNotFound() {
        when(repository.findById(50L)).thenReturn(Optional.empty());
        assertThrows(CoursePlanNotFoundException.class, () -> simulationService.simulate(50L, null));
    }

    @Test
    void shouldSimulateWithoutOverrides() {
        when(repository.findById(10L)).thenReturn(Optional.of(coursePlan));
        var response = simulationService.simulate(10L, null);
        assertNotNull(response);
    }

    @Test
    void shouldCloneGradesWhenPresent() {
        Assessment prova = Assessment.builder().id(11L).name("Prova").weight(new WeightValue(0.5)).grade(new GradeValue(9.0)).build();
        Assessment projeto = Assessment.builder().id(12L).name("Projeto").weight(new WeightValue(0.5)).build();
        CoursePlan planWithGrades = CoursePlan.builder().id(20L).targetAverage(7.0).assessments(List.of(prova, projeto)).build();
        prova.setCoursePlan(planWithGrades);
        projeto.setCoursePlan(planWithGrades);

        when(repository.findById(20L)).thenReturn(Optional.of(planWithGrades));

        var response = simulationService.simulate(20L, null);
        assertNotNull(response.getActivities());
    }

    @Test
    void shouldFailPersistWhenPlanMissing() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(CoursePlanNotFoundException.class, () -> simulationService.persistSimulation(77L, null));
    }
}

package com.lab4.calculator.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lab4.calculator.application.dto.AssessmentRequest;
import com.lab4.calculator.application.dto.CoursePlanRequest;
import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.value.WeightValue;
import com.lab4.calculator.domain.repository.CoursePlanRepository;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoursePlanServiceTest {

    @Mock
    private CoursePlanRepository repository;

    private CoursePlanService service;

    @BeforeEach
    void setup() {
        service = new CoursePlanService(repository);
    }

    @Test
    void shouldCreateCoursePlan() {
        CoursePlanRequest request = new CoursePlanRequest();
        request.setStudent("Aluno");
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setName("Prova");
        assessmentRequest.setWeight(0.5);
        request.setAssessments(List.of(assessmentRequest));

        CoursePlan saved = CoursePlan.builder().id(1L).student("Aluno").targetAverage(7.0).assessments(List.of()).build();
        when(repository.save(any(CoursePlan.class))).thenReturn(saved);

        CoursePlan result = service.create(request);

        assertEquals(1L, result.getId());
        assertEquals("Aluno", result.getStudent());
    }

    @Test
    void shouldUpdateGrades() {
        Assessment prova = Assessment.builder().id(1L).name("Prova").weight(new WeightValue(0.4)).build();
        Assessment trabalho = Assessment.builder().id(2L).name("Trabalho").weight(new WeightValue(0.6)).build();
        CoursePlan plan = CoursePlan.builder().id(10L).targetAverage(7.0).assessments(List.of(prova, trabalho)).build();
        prova.setCoursePlan(plan);
        trabalho.setCoursePlan(plan);

        when(repository.findById(10L)).thenReturn(Optional.of(plan));
        when(repository.save(any(CoursePlan.class))).thenReturn(plan);

        CoursePlan updated = service.updateGrades(10L, Arrays.asList(1L, 2L), Arrays.asList(8.0, 7.0));

        assertEquals(8.0, updated.getAssessments().get(0).getGrade().getValue());
        assertEquals(7.0, updated.getAssessments().get(1).getGrade().getValue());
    }

    @Test
    void shouldReturnAllPlans() {
        CoursePlan plan = CoursePlan.builder().id(1L).targetAverage(7.0).assessments(List.of()).build();
        when(repository.findAll()).thenReturn(List.of(plan));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void shouldThrowWhenPlanNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CoursePlanNotFoundException.class, () -> service.findById(99L));
    }
}

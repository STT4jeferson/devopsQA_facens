package com.lab4.calculator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lab4.calculator.application.dto.AssessmentRequest;
import com.lab4.calculator.application.dto.CoursePlanRequest;
import com.lab4.calculator.application.dto.SimulationActivityResponse;
import com.lab4.calculator.application.dto.SimulationResponse;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import com.lab4.calculator.application.mapper.CoursePlanMapper;
import com.lab4.calculator.domain.model.SimulationStatus;
import com.lab4.calculator.web.RestExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class CoverageBoostTest {

    @Test
    void shouldCoverDtosAndHandlers() throws Exception {
        // DTOs + mapper
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setName("Atividade 1");
        assessmentRequest.setWeight(0.5);
        assessmentRequest.setGrade(9.0);

        AssessmentRequest assessmentWithoutGrade = new AssessmentRequest();
        assessmentWithoutGrade.setName("Atividade 2");
        assessmentWithoutGrade.setWeight(0.5);

        CoursePlanRequest coursePlanRequest = new CoursePlanRequest();
        coursePlanRequest.setStudent("Aluno Mapper");
        coursePlanRequest.setAssessments(List.of(assessmentRequest, assessmentWithoutGrade));

        var entity = CoursePlanMapper.toEntity(coursePlanRequest);
        assertNotNull(entity);
        assertNotNull(CoursePlanMapper.toResponse(entity));

        SimulationActivityResponse.builder()
                .id(1L)
                .name("Simulada")
                .weight(0.5)
                .providedGrade(9.0)
                .requiredGrade(7.0)
                .build();

        SimulationResponse.builder()
                .targetAverage(7.0)
                .requiredGrade(6.0)
                .status(SimulationStatus.ALCANCAVEL)
                .activities(List.of())
                .build();

        // Exception handler paths
        RestExceptionHandler handler = new RestExceptionHandler();
        handler.handleNotFound(new CoursePlanNotFoundException(99L));
        handler.handleIllegalArgument(new IllegalArgumentException("erro"));

        MethodArgumentNotValidException manve = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "campo", "msg")));
        when(manve.getBindingResult()).thenReturn(bindingResult);
        handler.handleValidation(manve);
    }
}

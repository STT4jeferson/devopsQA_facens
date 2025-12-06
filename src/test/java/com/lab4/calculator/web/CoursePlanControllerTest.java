package com.lab4.calculator.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab4.calculator.application.dto.CoursePlanRequest;
import com.lab4.calculator.application.dto.SimulationActivityResponse;
import com.lab4.calculator.application.dto.SimulationRequest;
import com.lab4.calculator.application.dto.SimulationResponse;
import com.lab4.calculator.domain.model.SimulationStatus;
import com.lab4.calculator.domain.model.value.WeightValue;
import com.lab4.calculator.application.service.CoursePlanService;
import com.lab4.calculator.application.service.SimulationService;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import com.lab4.calculator.domain.model.CoursePlan;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CoursePlanController.class)
class CoursePlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoursePlanService coursePlanService;

    @MockBean
    private SimulationService simulationService;

    @Test
    void shouldCreateCoursePlan() throws Exception {
        CoursePlanRequest request = new CoursePlanRequest();
        request.setStudent("Aluno");
        var assessment = new com.lab4.calculator.application.dto.AssessmentRequest();
        assessment.setName("Prova");
        assessment.setWeight(0.4);
        assessment.setGrade(8.0);
        request.setAssessments(List.of(assessment));

        var savedEntity = com.lab4.calculator.application.mapper.CoursePlanMapper.toEntity(request);
        savedEntity.setId(1L);

        when(coursePlanService.create(any(CoursePlanRequest.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/v1/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.student").value("Aluno"));
    }

    @Test
    void shouldSimulatePlan() throws Exception {
        SimulationActivityResponse activity = SimulationActivityResponse.builder()
                .id(1L)
                .name("Prova")
                .weight(new WeightValue(0.4).getValue())
                .requiredGrade(7.0)
                .build();
        SimulationResponse response = SimulationResponse.builder()
                .targetAverage(7.0)
                .requiredGrade(7.0)
                .status(SimulationStatus.ALCANCAVEL)
                .activities(List.of(activity))
                .build();

        when(simulationService.simulate(eq(1L), any(SimulationRequest.class))).thenReturn(response);

        com.lab4.calculator.application.dto.SimulationGradeRequest gradeRequest = new com.lab4.calculator.application.dto.SimulationGradeRequest();
        gradeRequest.setAssessmentId(1L);
        gradeRequest.setGrade(8.0);
        SimulationRequest request = new SimulationRequest();
        request.setGrades(List.of(gradeRequest));

        mockMvc.perform(post("/api/v1/plans/1/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALCANCAVEL"));
    }

    @Test
    void shouldReturnPlanById() throws Exception {
        CoursePlan plan = CoursePlan.builder().id(5L).student("Aluno").targetAverage(7.0).assessments(List.of()).build();
        when(coursePlanService.findById(5L)).thenReturn(plan);

        mockMvc.perform(get("/api/v1/plans/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student").value("Aluno"));
    }

    @Test
    void shouldHandleNotFound() throws Exception {
        when(coursePlanService.findById(99L)).thenThrow(new CoursePlanNotFoundException(99L));

        mockMvc.perform(get("/api/v1/plans/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("não encontrado")));
    }

    @Test
    void shouldHandleIllegalArgumentFromService() throws Exception {
        com.lab4.calculator.application.dto.SimulationGradeRequest gradeRequest = new com.lab4.calculator.application.dto.SimulationGradeRequest();
        gradeRequest.setAssessmentId(1L);
        gradeRequest.setGrade(8.0);
        SimulationRequest request = new SimulationRequest();
        request.setGrades(List.of(gradeRequest));

        when(simulationService.simulate(eq(2L), any(SimulationRequest.class))).thenThrow(new IllegalArgumentException("erro"));

        mockMvc.perform(post("/api/v1/plans/2/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPersistSimulationWhenFlagIsTrue() throws Exception {
        com.lab4.calculator.application.dto.SimulationGradeRequest gradeRequest = new com.lab4.calculator.application.dto.SimulationGradeRequest();
        gradeRequest.setAssessmentId(3L);
        gradeRequest.setGrade(9.5);
        SimulationRequest request = new SimulationRequest();
        request.setGrades(List.of(gradeRequest));

        CoursePlan plan = CoursePlan.builder().id(3L).targetAverage(7.0).assessments(List.of()).build();
        when(simulationService.persistSimulation(eq(3L), any(SimulationRequest.class))).thenReturn(plan);
        when(simulationService.simulate(eq(3L), any(SimulationRequest.class))).thenReturn(SimulationResponse.builder()
                .targetAverage(7.0)
                .requiredGrade(7.0)
                .status(SimulationStatus.ALCANCAVEL)
                .activities(List.of())
                .build());

        mockMvc.perform(post("/api/v1/plans/3/simulate?persist=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredGrade").value(7.0));
    }

    @Test
    void shouldListPlans() throws Exception {
        com.lab4.calculator.domain.model.CoursePlan plan = com.lab4.calculator.domain.model.CoursePlan.builder()
                .id(1L)
                .student("Aluno")
                .targetAverage(7.0)
                .assessments(List.of())
                .build();
        when(coursePlanService.findAll()).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/v1/plans"))
                .andExpect(status().isOk());
    }
}

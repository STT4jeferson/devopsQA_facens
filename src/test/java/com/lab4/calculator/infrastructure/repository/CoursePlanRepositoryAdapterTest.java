package com.lab4.calculator.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lab4.calculator.domain.model.Assessment;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.model.value.WeightValue;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(CoursePlanRepositoryAdapter.class)
class CoursePlanRepositoryAdapterTest {

    @Autowired
    private CoursePlanRepositoryAdapter repositoryAdapter;

    @Autowired
    private CoursePlanJpaRepository jpaRepository;

    @Test
    void shouldPersistCoursePlanWithAssessments() {
        CoursePlan coursePlan = buildPlan();

        CoursePlan saved = repositoryAdapter.save(coursePlan);

        assertNotNull(saved.getId());
        assertEquals(2, saved.getAssessments().size());

        Optional<CoursePlan> reloaded = jpaRepository.findById(saved.getId());
        assertEquals("Aluno Teste", reloaded.orElseThrow().getStudent());
        assertEquals(true, repositoryAdapter.findById(saved.getId()).isPresent());

        assertEquals(1, repositoryAdapter.findAll().size());

        repositoryAdapter.deleteById(saved.getId());
        assertEquals(0, repositoryAdapter.findAll().size());
    }

    private CoursePlan buildPlan() {
        Assessment prova = Assessment.builder()
                .name("Prova")
                .weight(new WeightValue(0.4))
                .grade(new GradeValue(8.0))
                .build();
        Assessment trabalho = Assessment.builder()
                .name("Trabalho")
                .weight(new WeightValue(0.6))
                .build();

        CoursePlan plan = CoursePlan.builder()
                .student("Aluno Teste")
                .targetAverage(7.0)
                .build();
        prova.setCoursePlan(plan);
        trabalho.setCoursePlan(plan);
        plan.getAssessments().add(prova);
        plan.getAssessments().add(trabalho);
        return plan;
    }
}

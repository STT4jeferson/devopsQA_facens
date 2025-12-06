package com.lab4.calculator.application.service;

import com.lab4.calculator.application.dto.CoursePlanRequest;
import com.lab4.calculator.application.exception.CoursePlanNotFoundException;
import com.lab4.calculator.application.mapper.CoursePlanMapper;
import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.repository.CoursePlanRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePlanService {

    private final CoursePlanRepository coursePlanRepository;

    @Transactional
    public CoursePlan create(CoursePlanRequest request) {
        CoursePlan coursePlan = CoursePlanMapper.toEntity(request);
        return coursePlanRepository.save(coursePlan);
    }

    public List<CoursePlan> findAll() {
        return coursePlanRepository.findAll();
    }

    public CoursePlan findById(Long id) {
        return coursePlanRepository.findById(id)
                .orElseThrow(() -> new CoursePlanNotFoundException(id));
    }

    @Transactional
    public CoursePlan updateGrades(Long id, List<Long> assessmentIds, List<Double> grades) {
        CoursePlan coursePlan = findById(id);
        for (int i = 0; i < assessmentIds.size(); i++) {
            Long assessmentId = assessmentIds.get(i);
            Double grade = grades.get(i);
            coursePlan.getAssessments().stream()
                    .filter(assessment -> assessmentId.equals(assessment.getId()))
                    .findFirst()
                    .ifPresent(assessment -> assessment.setGrade(new GradeValue(grade)));
        }
        return coursePlanRepository.save(coursePlan);
    }
}

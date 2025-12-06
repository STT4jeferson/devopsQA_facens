package com.lab4.calculator.infrastructure.repository;

import com.lab4.calculator.domain.model.CoursePlan;
import com.lab4.calculator.domain.repository.CoursePlanRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoursePlanRepositoryAdapter implements CoursePlanRepository {

    private final CoursePlanJpaRepository jpaRepository;

    @Override
    public CoursePlan save(CoursePlan coursePlan) {
        return jpaRepository.save(coursePlan);
    }

    @Override
    public Optional<CoursePlan> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CoursePlan> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

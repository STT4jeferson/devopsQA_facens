package com.lab4.calculator.domain.repository;

import com.lab4.calculator.domain.model.CoursePlan;
import java.util.List;
import java.util.Optional;

public interface CoursePlanRepository {

    CoursePlan save(CoursePlan coursePlan);

    Optional<CoursePlan> findById(Long id);

    List<CoursePlan> findAll();

    void deleteById(Long id);
}

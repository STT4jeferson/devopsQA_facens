package com.lab4.calculator.infrastructure.repository;

import com.lab4.calculator.domain.model.CoursePlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePlanJpaRepository extends JpaRepository<CoursePlan, Long> {
}

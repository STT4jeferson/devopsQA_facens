package com.lab4.calculator.domain.model;

import com.lab4.calculator.domain.model.value.WeightValue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String student;

    @Builder.Default
    private Double targetAverage = 7.0;

    @OneToMany(mappedBy = "coursePlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Assessment> assessments = new ArrayList<>();

    public void addAssessment(Assessment assessment) {
        assessment.setCoursePlan(this);
        assessments.add(assessment);
    }

    public double getTotalWeight() {
        return assessments.stream()
                .map(Assessment::getWeight)
                .mapToDouble(WeightValue::getValue)
                .sum();
    }
}

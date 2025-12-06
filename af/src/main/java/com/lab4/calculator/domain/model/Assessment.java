package com.lab4.calculator.domain.model;

import com.lab4.calculator.domain.model.value.GradeValue;
import com.lab4.calculator.domain.model.value.WeightValue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "weight"))
    private WeightValue weight;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "grade"))
    private GradeValue grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_plan_id")
    @ToString.Exclude
    private CoursePlan coursePlan;

    public boolean hasGrade() {
        return grade != null && !grade.isMissing();
    }
}

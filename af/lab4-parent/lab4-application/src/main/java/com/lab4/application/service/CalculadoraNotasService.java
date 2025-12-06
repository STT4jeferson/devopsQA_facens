package com.lab4.application.service;

import java.util.HashMap;
import java.util.Map;


public class CalculadoraNotasService {

    public static class Result {
        private final double required;
        private final String status;
        private final Map<String, Double> otherMinimums;

        public Result(double required, String status, Map<String, Double> otherMinimums) {
            this.required = required;
            this.status = status;
            this.otherMinimums = otherMinimums;
        }

        public double getRequired() {
            return required;
        }

        public String getStatus() {
            return status;
        }

        public Map<String, Double> getOtherMinimums() {
            return otherMinimums;
        }
    }

    public Result simulateGrade(String activityName,
                                double grade,
                                Map<String, Double> weights,
                                Map<String, Double> currentGrades) {
        validateInputs(weights);

        Map<String, Double> grades = mergeGrades(currentGrades, activityName, grade);

        Map<String, Double> mins = calculateMinimums(weights, grades);

        double requiredForSimulated = mins.getOrDefault(activityName, Double.POSITIVE_INFINITY);
        String status = statusFromRequired(requiredForSimulated);

        return new Result(requiredForSimulated, status, mins);
    }

    private void validateInputs(Map<String, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            throw new IllegalArgumentException("weights must not be null or empty");
        }
    }

    private Map<String, Double> mergeGrades(Map<String, Double> currentGrades, String activityName, double grade) {
        Map<String, Double> grades = new HashMap<>();
        if (currentGrades != null) grades.putAll(currentGrades);
        grades.put(activityName, grade);
        return grades;
    }

    private Map<String, Double> calculateMinimums(Map<String, Double> weights, Map<String, Double> grades) {
        Map<String, Double> mins = new HashMap<>();
        for (String a : weights.keySet()) {
            double w = weights.getOrDefault(a, 0.0);
            if (w == 0.0) {
                mins.put(a, Double.POSITIVE_INFINITY);
                continue;
            }
            double sumOthers = sumContributionExcept(weights, grades, a);
            double required = (7.0 - sumOthers) / w;
            mins.put(a, required);
        }
        return mins;
    }

    private double sumContributionExcept(Map<String, Double> weights, Map<String, Double> grades, String excluded) {
        double sum = 0.0;
        for (Map.Entry<String, Double> e : weights.entrySet()) {
            String other = e.getKey();
            if (other.equals(excluded)) continue;
            double otherW = e.getValue();
            double otherGrade = grades.getOrDefault(other, 0.0);
            sum += otherW * otherGrade;
        }
        return sum;
    }

    private String statusFromRequired(double required) {
        return (required > 10.0) ? "inviável" : "alcançável";
    }
}

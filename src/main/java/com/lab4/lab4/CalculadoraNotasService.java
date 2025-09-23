package com.lab4.lab4;

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
        Map<String, Double> grades = new HashMap<>();
        if (currentGrades != null) {
            grades.putAll(currentGrades);
        }
        grades.put(activityName, grade);

        Map<String, Double> mins = new HashMap<>();

        for (String a : weights.keySet()) {
            double w = weights.getOrDefault(a, 0.0);
            if (w == 0.0) {
                mins.put(a, Double.POSITIVE_INFINITY);
                continue;
            }

            double sumOthers = 0.0;
            for (Map.Entry<String, Double> e : weights.entrySet()) {
                String other = e.getKey();
                if (other.equals(a)) continue;
                double otherW = e.getValue();
                double otherGrade = grades.getOrDefault(other, 0.0);
                sumOthers += otherW * otherGrade;
            }

            double required = (7.0 - sumOthers) / w;
            mins.put(a, required);
        }

        double requiredForSimulated = mins.getOrDefault(activityName, Double.POSITIVE_INFINITY);
        String status = (requiredForSimulated > 10.0) ? "inviável" : "alcançável";

        return new Result(requiredForSimulated, status, mins);
    }

}

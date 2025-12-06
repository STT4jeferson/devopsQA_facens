package com.lab4.calculator.application.exception;

public class CoursePlanNotFoundException extends RuntimeException {
    public CoursePlanNotFoundException(Long id) {
        super("Plano de curso com id %d não encontrado".formatted(id));
    }
}

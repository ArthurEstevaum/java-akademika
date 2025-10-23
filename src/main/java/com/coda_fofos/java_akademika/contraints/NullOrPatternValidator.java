package com.coda_fofos.java_akademika.contraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrPatternValidator implements ConstraintValidator<NullOrPattern, String> {
    private String pattern;

    @Override
    public void initialize(NullOrPattern constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.matches(pattern);
    }
}
package com.coda_fofos.java_akademika.contraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NullOrPatternValidator implements ConstraintValidator<NullOrPattern, String> {
    private Pattern compiledPattern;

    @Override
    public void initialize(NullOrPattern constraintAnnotation) {
        final String pattern = constraintAnnotation.pattern();
        this.compiledPattern = Pattern.compile(pattern);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || compiledPattern.matcher(value).matches();
    }
}

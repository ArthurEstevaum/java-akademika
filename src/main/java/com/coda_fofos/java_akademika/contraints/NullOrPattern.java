package com.coda_fofos.java_akademika.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NullOrPatternValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrPattern {
    String message() default "The field must be null or match the Regex pattern";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String pattern();
}
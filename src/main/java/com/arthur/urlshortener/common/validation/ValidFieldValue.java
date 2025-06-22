package com.arthur.urlshortener.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {ValidFieldValueValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFieldValue {
    String message() default "Invalid field value. Allowed values: {allowedFields}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] allowedFields() default {};
}

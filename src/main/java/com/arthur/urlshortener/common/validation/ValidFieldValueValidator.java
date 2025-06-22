package com.arthur.urlshortener.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidFieldValueValidator implements ConstraintValidator<ValidFieldValue, String> {

    private List<String> allowedFields;

    @Override
    public void initialize(ValidFieldValue constraintAnnotation) {
        this.allowedFields = List.of(constraintAnnotation.allowedFields());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = allowedFields.contains(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            String message = String.format("Invalid field value. Allowed values: %s",
                    String.join(", ", allowedFields));
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isValid;
    }
}

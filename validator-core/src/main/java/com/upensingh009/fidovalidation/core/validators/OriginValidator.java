package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;

import java.time.Duration;
import java.time.Instant;

/**
 * Validates origin equals expected origin.
 */
public class OriginValidator implements Validator<OriginValidator.Input> {
    public static record Input(String expectedOrigin, String actualOrigin) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        boolean pass = input.expectedOrigin() != null && input.expectedOrigin().equalsIgnoreCase(input.actualOrigin());
        Instant end = Instant.now();
        return new ValidationResult("OriginValidator",
                pass ? ValidationStatus.PASS : ValidationStatus.FAIL,
                input.expectedOrigin(),
                input.actualOrigin(),
                pass ? "origin matched" : "origin mismatch",
                Duration.between(start, end));
    }
}

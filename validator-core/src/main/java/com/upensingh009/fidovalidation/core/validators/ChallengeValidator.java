package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;

import java.time.Duration;
import java.time.Instant;

/**
 * Validates that the provided challenge equals the expected challenge.
 */
public class ChallengeValidator implements Validator<ChallengeValidator.Input> {

    public static record Input(String expected, String actual) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        boolean pass = input.expected() != null && input.expected().equals(input.actual());
        Instant end = Instant.now();
        return new ValidationResult("ChallengeValidator",
                pass ? ValidationStatus.PASS : ValidationStatus.FAIL,
                input.expected(),
                input.actual(),
                pass ? "challenge matched" : "challenge mismatch",
                Duration.between(start, end));
    }
}

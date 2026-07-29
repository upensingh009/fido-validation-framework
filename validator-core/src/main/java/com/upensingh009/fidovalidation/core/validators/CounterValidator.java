package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;

import java.time.Duration;
import java.time.Instant;

/**
 * CounterValidator ensures signCount monotonicity compared to stored counter.
 */
public class CounterValidator implements Validator<CounterValidator.Input> {
    public static record Input(int storedCounter, int currentCounter) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        boolean pass = input.currentCounter() >= input.storedCounter();
        Instant end = Instant.now();
        return new ValidationResult("CounterValidator",
                pass ? ValidationStatus.PASS : ValidationStatus.FAIL,
                String.valueOf(input.storedCounter()),
                String.valueOf(input.currentCounter()),
                pass ? "counter OK" : "counter decreased (possible cloned authenticator)",
                Duration.between(start, end));
    }
}

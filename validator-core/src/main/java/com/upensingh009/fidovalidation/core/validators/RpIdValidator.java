package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;
import com.upensingh009.fidovalidation.parser.authenticator.AuthenticatorData;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

/**
 * Validates rpIdHash in authenticatorData against expected rpIdHash (hex or base64)
 */
public class RpIdValidator implements Validator<RpIdValidator.Input> {
    public static record Input(String expectedRpIdHashHex, AuthenticatorData authData) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();

        String actualHex = input.authData().rpIdHashHex();
        boolean pass = actualHex.equalsIgnoreCase(input.expectedRpIdHashHex);

        Instant end = Instant.now();
        return new ValidationResult("RpIdValidator",
                pass ? ValidationStatus.PASS : ValidationStatus.FAIL,
                input.expectedRpIdHashHex(),
                actualHex,
                pass ? "rpIdHash matched" : "rpIdHash mismatch",
                Duration.between(start, end));
    }
}

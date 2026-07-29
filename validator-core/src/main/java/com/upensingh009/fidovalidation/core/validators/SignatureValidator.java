package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;

import java.time.Duration;
import java.time.Instant;

/**
 * SignatureValidator currently provides a placeholder result indicating verification is not yet implemented.
 * Future implementation will verify signatures using COSE key parameters and BouncyCastle or WebAuthn4J utilities.
 */
public class SignatureValidator implements Validator<SignatureValidator.Input> {

    public static record Input(byte[] publicKeyCbor, byte[] signature, byte[] signedData, int coseAlg) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        // TODO: implement actual signature verification using coseAlg and publicKeyCbor
        Instant end = Instant.now();
        return new ValidationResult("SignatureValidator",
                ValidationStatus.SKIPPED,
                "signature verification implemented",
                "not implemented",
                "signature verification deferred - not implemented yet",
                Duration.between(start, end));
    }
}

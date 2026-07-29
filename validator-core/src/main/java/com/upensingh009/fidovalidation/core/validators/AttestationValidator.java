package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;
import com.upensingh009.fidovalidation.parser.attestation.Attestation;

import java.time.Duration;
import java.time.Instant;

/**
 * AttestationValidator - minimal implementation to validate "none" and basic "packed" structure.
 * Detailed crypto verification is delegated to SignatureValidator (not fully implemented yet).
 */
public class AttestationValidator implements Validator<Attestation> {

    @Override
    public ValidationResult validate(Attestation attestation) {
        Instant start = Instant.now();
        if (attestation == null) {
            return new ValidationResult("AttestationValidator", ValidationStatus.FAIL, "attestation object", "null", "attestation missing", Duration.between(start, Instant.now()));
        }

        String fmt = attestation.fmt();
        if ("none".equals(fmt)) {
            // none attestation has empty attStmt
            Instant end = Instant.now();
            return new ValidationResult("AttestationValidator", ValidationStatus.PASS, "fmt=none, attStmt empty", "fmt=none", "none attestation accepted", Duration.between(start, end));
        }

        if ("packed".equals(fmt)) {
            // minimal check: attStmt should be a map and contain sig
            Object attStmt = attestation.attStmt();
            if (attStmt instanceof java.util.Map) {
                java.util.Map<?, ?> m = (java.util.Map<?, ?>) attStmt;
                if (m.containsKey("sig") || m.containsKey("signature")) {
                    return new ValidationResult("AttestationValidator", ValidationStatus.SKIPPED, "packed attestation - crypto verification required", "packed", "packed attestation detected - deferred crypto check", Duration.between(start, Instant.now()));
                }
            }
            return new ValidationResult("AttestationValidator", ValidationStatus.FAIL, "packed attestation with sig", "missing", "packed attestation missing signature", Duration.between(start, Instant.now()));
        }

        // unsupported formats -> SKIPPED
        Instant end = Instant.now();
        return new ValidationResult("AttestationValidator", ValidationStatus.SKIPPED, "supported fmts: none, packed", fmt, "format not fully supported yet", Duration.between(start, end));
    }
}

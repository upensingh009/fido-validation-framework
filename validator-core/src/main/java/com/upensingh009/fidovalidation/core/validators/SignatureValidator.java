package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.util.CoseKeyUtil;
import com.upensingh009.fidovalidation.core.validation.Validator;

import java.security.PublicKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * SignatureValidator verifies signatures using a COSE-encoded public key map.
 */
public class SignatureValidator implements Validator<SignatureValidator.Input> {

    public static record Input(Map<?, ?> coseKey, byte[] signature, byte[] signedData, int coseAlg) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        try {
            PublicKey pub = CoseKeyUtil.convertCoseToPublicKey(input.coseKey());
            String alg = mapCoseAlgToJava(input.coseAlg());
            if (alg == null) {
                return new ValidationResult("SignatureValidator", ValidationStatus.SKIPPED, "alg mapping for coseAlg", String.valueOf(input.coseAlg()), "unsupported alg", Duration.between(start, Instant.now()));
            }
            Signature sig = Signature.getInstance(alg);
            sig.initVerify(pub);
            sig.update(input.signedData());
            boolean ok = sig.verify(input.signature());
            Instant end = Instant.now();
            return new ValidationResult("SignatureValidator", ok ? ValidationStatus.PASS : ValidationStatus.FAIL, "signature valid", String.valueOf(ok), ok ? "signature verified" : "signature invalid", Duration.between(start, end));
        } catch (Exception e) {
            Instant end = Instant.now();
            return new ValidationResult("SignatureValidator", ValidationStatus.FAIL, "signature verification", "error", "exception: " + e.getMessage(), Duration.between(start, end));
        }
    }

    private String mapCoseAlgToJava(int coseAlg) {
        return switch (coseAlg) {
            case -7 -> "SHA256withECDSA"; // ES256
            case -35 -> "SHA384withECDSA"; // ES384
            case -36 -> "SHA512withECDSA"; // ES512
            case -257 -> "SHA256withRSA"; // RS256
            default -> null;
        };
    }
}

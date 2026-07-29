package com.upensingh009.fidovalidation.core.validators;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.common.validation.ValidationStatus;
import com.upensingh009.fidovalidation.core.validation.Validator;
import com.upensingh009.fidovalidation.parser.clientdata.ClientData;

import java.time.Duration;
import java.time.Instant;

/**
 * Validates client data: type and origin/challenge minimal checks.
 */
public class ClientDataValidator implements Validator<ClientDataValidator.Input> {
    public static record Input(String expectedType, String expectedChallenge, ClientData clientData) {
    }

    @Override
    public ValidationResult validate(Input input) {
        Instant start = Instant.now();
        boolean typeOk = input.expectedType() != null && input.expectedType().equals(clientDataSafe(input.clientData()).type());
        boolean challengeOk = input.expectedChallenge() != null && input.expectedChallenge().equals(clientDataSafe(input.clientData()).challenge());
        boolean pass = typeOk && challengeOk;
        Instant end = Instant.now();
        return new ValidationResult("ClientDataValidator",
                pass ? ValidationStatus.PASS : ValidationStatus.FAIL,
                String.format("type=%s,challenge=%s", input.expectedType(), input.expectedChallenge()),
                String.format("type=%s,challenge=%s", clientDataSafe(input.clientData()).type(), clientDataSafe(input.clientData()).challenge()),
                pass ? "clientData validated" : "clientData validation failed",
                Duration.between(start, end));
    }

    private ClientData clientDataSafe(ClientData cd) {
        if (cd == null) return new ClientData("", "", "", "");
        return cd;
    }
}

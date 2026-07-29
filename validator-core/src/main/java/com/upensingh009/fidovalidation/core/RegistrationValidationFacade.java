package com.upensingh009.fidovalidation.core;

import com.upensingh009.fidovalidation.core.validators.ChallengeValidator;
import com.upensingh009.fidovalidation.core.validators.ClientDataValidator;
import com.upensingh009.fidovalidation.core.validators.OriginValidator;
import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.core.engine.ValidatorEngine;
import com.upensingh009.fidovalidation.parser.clientdata.ClientData;

import java.util.List;

/**
 * Facade that demonstrates how validators can be used together for a registration flow.
 */
public class RegistrationValidationFacade {
    private final ChallengeValidator challengeValidator = new ChallengeValidator();
    private final OriginValidator originValidator = new OriginValidator();
    private final ClientDataValidator clientDataValidator = new ClientDataValidator();

    public ValidatorEngine validateRegistration(String expectedChallenge, String expectedOrigin, ClientData clientData) {
        ValidatorEngine engine = new ValidatorEngine();

        ValidationResult r1 = challengeValidator.validate(new ChallengeValidator.Input(expectedChallenge, clientData.challenge()));
        engine.addResult(r1);

        ValidationResult r2 = originValidator.validate(new OriginValidator.Input(expectedOrigin, clientData.origin()));
        engine.addResult(r2);

        ValidationResult r3 = clientDataValidator.validate(new ClientDataValidator.Input("webauthn.create", expectedChallenge, clientData));
        engine.addResult(r3);

        return engine;
    }
}

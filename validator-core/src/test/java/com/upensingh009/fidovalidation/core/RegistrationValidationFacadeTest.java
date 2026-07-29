package com.upensingh009.fidovalidation.core;

import com.upensingh009.fidovalidation.core.RegistrationValidationFacade;
import com.upensingh009.fidovalidation.common.validation.ValidationResult;
import com.upensingh009.fidovalidation.core.engine.ValidatorEngine;
import com.upensingh009.fidovalidation.parser.clientdata.ClientData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationValidationFacadeTest {
    @Test
    public void testValidationFacadePasses() {
        RegistrationValidationFacade facade = new RegistrationValidationFacade();
        ClientData cd = new ClientData("webauthn.create", "MTIzNDU2", "https://example.com", "{}");
        ValidatorEngine engine = facade.validateRegistration("MTIzNDU2", "https://example.com", cd);
        assertThat(engine.getResults()).hasSize(3);
        assertThat(engine.allPassed()).isTrue();
    }

    @Test
    public void testValidationFacadeFailsOnChallenge() {
        RegistrationValidationFacade facade = new RegistrationValidationFacade();
        ClientData cd = new ClientData("webauthn.create", "BAD", "https://example.com", "{}");
        ValidatorEngine engine = facade.validateRegistration("MTIzNDU2", "https://example.com", cd);
        assertThat(engine.getResults()).hasSize(3);
        assertThat(engine.allPassed()).isFalse();
    }
}

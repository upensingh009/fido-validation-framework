package com.upensingh009.fidovalidation.core.validators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SignatureValidatorTest {

    @Test
    public void signatureValidatorReturnsSkipped() {
        SignatureValidator sv = new SignatureValidator();
        SignatureValidator.Input input = new SignatureValidator.Input(new byte[0], new byte[0], new byte[0], -7);
        var res = sv.validate(input);
        assertThat(res.status().name()).isEqualTo("SKIPPED");
    }
}

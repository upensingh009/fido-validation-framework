package com.upensingh009.fidovalidation.parser;

import com.upensingh009.fidovalidation.parser.clientdata.ClientData;
import com.upensingh009.fidovalidation.parser.clientdata.ClientDataParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientDataParserTest {
    @Test
    public void parseSimpleClientData() throws Exception {
        String json = "{\"type\":\"webauthn.create\",\"challenge\":\"MTIzNDU2\",\"origin\":\"https://example.com\"}";
        ClientDataParser p = new ClientDataParser();
        ClientData cd = p.parse(json.getBytes());
        assertThat(cd.type()).isEqualTo("webauthn.create");
        assertThat(cd.origin()).isEqualTo("https://example.com");
        assertThat(cd.challenge()).isNotBlank();
    }
}

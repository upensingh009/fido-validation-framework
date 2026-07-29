package com.upensingh009.fidovalidation.parser.authenticator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticatorDataParserTest {

    @Test
    public void parseAuthDataWithAttestedCredential() throws Exception {
        byte[] rpIdHash = new byte[32];
        for (int i = 0; i < rpIdHash.length; i++) rpIdHash[i] = (byte) i;
        byte flags = 0x40; // attested data present
        int signCount = 1;

        byte[] aaguid = new byte[16];
        for (int i = 0; i < aaguid.length; i++) aaguid[i] = (byte) (i + 1);

        byte[] credentialId = new byte[16];
        for (int i = 0; i < credentialId.length; i++) credentialId[i] = (byte) (i + 2);

        // credentialPublicKey CBOR
        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        Map<String, Object> cose = new HashMap<>();
        cose.put("kty", "EC2");
        cose.put("alg", -7);
        byte[] coseBytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            mapper.writeValue(bos, cose);
            coseBytes = bos.toByteArray();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(rpIdHash);
        out.write(flags);
        out.write(ByteBuffer.allocate(4).putInt(signCount).array());
        out.write(aaguid);
        out.write(ByteBuffer.allocate(2).putShort((short) credentialId.length).array());
        out.write(credentialId);
        out.write(coseBytes);

        byte[] authData = out.toByteArray();

        AuthenticatorDataParser parser = new AuthenticatorDataParser();
        AuthenticatorData ad = parser.parse(authData);

        assertThat(ad.attestedCredentialDataIncluded()).isTrue();
        assertThat(ad.attestedCredentialData()).isNotNull();
        assertThat(ad.attestedCredentialData().aaguid()).isEqualTo(aaguid);
        assertThat(ad.attestedCredentialData().credentialId()).isEqualTo(credentialId);
        assertThat(ad.attestedCredentialData().coseKey()).isNotNull();
    }
}

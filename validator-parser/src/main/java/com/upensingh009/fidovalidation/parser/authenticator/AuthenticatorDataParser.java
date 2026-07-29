package com.upensingh009.fidovalidation.parser.authenticator;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Parser for authenticatorData structure as defined in WebAuthn spec.
 * Parses rpIdHash (32), flags (1), signCount (4), and returns remaining attested data and extensions as raw bytes.
 */
public class AuthenticatorDataParser {

    public AuthenticatorData parse(byte[] authData) {
        if (authData == null || authData.length < 37) {
            throw new IllegalArgumentException("authData too short");
        }

        ByteBuffer buf = ByteBuffer.wrap(authData);
        byte[] rpIdHash = new byte[32];
        buf.get(rpIdHash);
        byte flags = buf.get();
        int signCount = buf.getInt();

        byte[] rest = new byte[buf.remaining()];
        buf.get(rest);

        boolean attestedCredentialDataIncluded = (flags & 0x40) != 0; // AT flag
        boolean extensionsIncluded = (flags & 0x80) != 0; // ED flag

        return new AuthenticatorData(rpIdHash, flags, signCount, attestedCredentialDataIncluded, extensionsIncluded, rest);
    }
}

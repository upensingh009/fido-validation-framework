package com.upensingh009.fidovalidation.parser.authenticator;

import com.upensingh009.fidovalidation.parser.cbor.CborParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Parser for authenticatorData structure as defined in WebAuthn spec.
 * Parses rpIdHash (32), flags (1), signCount (4), and attestedCredentialData when present.
 */
public class AuthenticatorDataParser {

    private final CborParser cborParser = new CborParser();

    public AuthenticatorData parse(byte[] authData) throws IOException {
        if (authData == null || authData.length < 37) {
            throw new IllegalArgumentException("authData too short");
        }

        ByteBuffer buf = ByteBuffer.wrap(authData);
        byte[] rpIdHash = new byte[32];
        buf.get(rpIdHash);
        byte flags = buf.get();
        int signCount = buf.getInt();

        int remainingLen = buf.remaining();
        byte[] remaining = new byte[remainingLen];
        buf.get(remaining);

        boolean attestedCredentialDataIncluded = (flags & 0x40) != 0; // AT flag
        boolean extensionsIncluded = (flags & 0x80) != 0; // ED flag

        AttestedCredentialData attested = null;
        byte[] extensions = null;

        if (attestedCredentialDataIncluded) {
            // Parse attestedCredentialData from remaining
            // Structure: aaguid (16) | credIdLen (2) | credId | credentialPublicKey (CBOR)
            ByteArrayInputStream in = new ByteArrayInputStream(remaining);
            byte[] aaguid = new byte[16];
            if (in.read(aaguid) != 16) throw new IllegalArgumentException("invalid aaguid length");

            byte[] lenBuf = new byte[2];
            if (in.read(lenBuf) != 2) throw new IllegalArgumentException("invalid credIdLen");
            int credIdLen = ((lenBuf[0] & 0xff) << 8) | (lenBuf[1] & 0xff);

            byte[] credId = new byte[credIdLen];
            if (in.read(credId) != credIdLen) throw new IllegalArgumentException("invalid credId length");

            // The rest of the stream should be credentialPublicKey CBOR (may be followed by extensions)
            byte[] rest = in.readAllBytes();

            // Try to decode credentialPublicKey as CBOR map. This will attempt to decode the full rest;
            // if extensions follow, they will be included — a more robust implementation would decode incrementally
            Map<String, Object> coseKey = null;
            try {
                coseKey = cborParser.parse(rest);
            } catch (IOException e) {
                // If CBOR decode fails, coseKey remains null but we still return attested data with raw bytes
            }

            attested = new AttestedCredentialData(aaguid, credId, rest, coseKey);
            extensions = new byte[0];
        } else if (extensionsIncluded) {
            // For simplicity, treat all remaining as extensions when no attested data present
            extensions = remaining;
        }

        return new AuthenticatorData(rpIdHash, flags, signCount, attestedCredentialDataIncluded, extensionsIncluded, attested, extensions);
    }
}

package com.upensingh009.fidovalidation.parser.attestation;

import java.util.Map;

/**
 * Very small attestation parser that attempts to detect attestation fmt name and extract attStmt
 * from the CBOR-decoded attestation object (map).
 */
public class AttestationParser {

    public static Attestation parse(Map<String, Object> attestationObject) {
        if (attestationObject == null) return null;
        Object fmt = attestationObject.get("fmt");
        Object attStmt = attestationObject.get("attStmt");
        Object authData = attestationObject.get("authData");
        String fmtStr = fmt == null ? "unknown" : fmt.toString();
        return new Attestation(fmtStr, attStmt, authData);
    }
}

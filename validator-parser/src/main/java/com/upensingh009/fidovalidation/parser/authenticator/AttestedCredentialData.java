package com.upensingh009.fidovalidation.parser.authenticator;

import java.util.Arrays;
import java.util.Map;

public record AttestedCredentialData(byte[] aaguid, byte[] credentialId, byte[] credentialPublicKeyCbor, Map<String, Object> coseKey) {

    public String aaguidHex() {
        StringBuilder sb = new StringBuilder();
        for (byte b : aaguid) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public String credentialIdBase64() {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(credentialId);
    }

    @Override
    public String toString() {
        return "AttestedCredentialData{" +
                "aaguid=" + aaguidHex() +
                ", credentialIdLen=" + (credentialId == null ? 0 : credentialId.length) +
                ", coseKeyPresent=" + (coseKey != null) +
                '}';
    }
}

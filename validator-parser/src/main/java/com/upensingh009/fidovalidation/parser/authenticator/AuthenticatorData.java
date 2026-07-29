package com.upensingh009.fidovalidation.parser.authenticator;

import java.util.Arrays;

public record AuthenticatorData(byte[] rpIdHash,
                                byte flags,
                                int signCount,
                                boolean attestedCredentialDataIncluded,
                                boolean extensionsIncluded,
                                byte[] remaining) {

    public String rpIdHashHex() {
        StringBuilder sb = new StringBuilder();
        for (byte b : rpIdHash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AuthenticatorData{" +
                "rpIdHash=" + rpIdHashHex() +
                ", flags=0x" + String.format("%02x", flags) +
                ", signCount=" + signCount +
                ", attested=" + attestedCredentialDataIncluded +
                ", ext=" + extensionsIncluded +
                ", remainingLen=" + (remaining == null ? 0 : remaining.length) +
                '}';
    }
}

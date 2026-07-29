package com.upensingh009.fidovalidation.parser.cose;

import java.util.Map;

/**
 * Very small COSE parser to extract common fields from COSE_Key structures represented as maps.
 * Expects the COSE key structure (as decoded from CBOR) to be provided as a Map.
 */
public final class CoseParser {

    private CoseParser() {
    }

    public static int getAlgorithm(Map<Object, Object> coseKey) {
        // COSE key uses label -1 for alg in many representations. This method is defensive.
        if (coseKey.containsKey(-1)) {
            Object alg = coseKey.get(-1);
            if (alg instanceof Number) return ((Number) alg).intValue();
            try {
                return Integer.parseInt(alg.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

}

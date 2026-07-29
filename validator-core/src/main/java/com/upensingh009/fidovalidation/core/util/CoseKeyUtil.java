package com.upensingh009.fidovalidation.core.util;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Map;

/**
 * Utility to convert COSE EC2 keys (as maps) into Java PublicKey instances.
 * Supports primary COSE labels: 1 (kty), -1 (crv), -2 (x), -3 (y) as numbers.
 */
public final class CoseKeyUtil {
    private CoseKeyUtil() {}

    public static PublicKey convertCoseToPublicKey(Map<?, ?> coseKey) throws Exception {
        if (coseKey == null) throw new IllegalArgumentException("coseKey is null");

        // kty
        Object ktyObj = getValue(coseKey, 1, "kty");
        int kty = ktyObj instanceof Number ? ((Number) ktyObj).intValue() : Integer.parseInt(ktyObj.toString());
        if (kty != 2) throw new IllegalArgumentException("Only EC2 keys supported in this converter (kty=2 expected)");

        Object crvObj = getValue(coseKey, -1, "crv");
        int crv = crvObj instanceof Number ? ((Number) crvObj).intValue() : Integer.parseInt(crvObj.toString());

        String curveName = mapCoseCurveToBcName(crv);
        if (curveName == null) throw new IllegalArgumentException("Unsupported COSE curve: " + crv);

        Object xObj = getValue(coseKey, -2, "x");
        Object yObj = getValue(coseKey, -3, "y");
        byte[] x = toByteArray(xObj);
        byte[] y = toByteArray(yObj);

        // Use BouncyCastle to obtain curve parameters, then construct a java.security.spec.ECParameterSpec
        ECNamedCurveParameterSpec bcSpec = ECNamedCurveTable.getParameterSpec(curveName);
        ECNamedCurveSpec params = new ECNamedCurveSpec(curveName, bcSpec.getCurve(), bcSpec.getG(), bcSpec.getN(), bcSpec.getH());

        BigInteger bx = new BigInteger(1, x);
        BigInteger by = new BigInteger(1, y);
        ECPoint w = new ECPoint(bx, by);

        ECPublicKeySpec pubSpec = new ECPublicKeySpec(w, params);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePublic(pubSpec);
    }

    private static String mapCoseCurveToBcName(int crv) {
        // COSE crv values: 1 = P-256, 2 = P-384, 3 = P-521, etc.
        return switch (crv) {
            case 1 -> "secp256r1"; // P-256
            case 2 -> "secp384r1"; // P-384
            case 3 -> "secp521r1"; // P-521
            default -> null;
        };
    }

    private static byte[] toByteArray(Object obj) {
        if (obj instanceof byte[]) return (byte[]) obj;
        if (obj instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) obj;
            byte[] b = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) b[i] = ((Number) list.get(i)).byteValue();
            return b;
        }
        throw new IllegalArgumentException("Unsupported key material type: " + (obj == null ? "null" : obj.getClass().getName()));
    }

    private static Object getValue(Map<?, ?> m, Object numericKey, String stringKey) {
        if (m.containsKey(numericKey)) return m.get(numericKey);
        if (m.containsKey(stringKey)) return m.get(stringKey);
        return null;
    }
}

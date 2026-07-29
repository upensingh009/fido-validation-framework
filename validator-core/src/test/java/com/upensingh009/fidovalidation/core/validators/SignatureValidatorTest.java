package com.upensingh009.fidovalidation.core.validators;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SignatureValidatorTest {

    @Test
    public void signatureValidatorVerifiesEcdsaP256() throws Exception {
        // generate keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new java.security.spec.ECGenParameterSpec("secp256r1"));
        KeyPair kp = kpg.generateKeyPair();
        PrivateKey priv = kp.getPrivate();
        ECPublicKey pub = (ECPublicKey) kp.getPublic();

        byte[] data = "hello world".getBytes();
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(priv);
        signer.update(data);
        byte[] signature = signer.sign();

        // Extract x and y coordinates
        byte[] x = trimTo32(pub.getW().getAffineX().toByteArray());
        byte[] y = trimTo32(pub.getW().getAffineY().toByteArray());

        Map<Object, Object> coseKey = new HashMap<>();
        coseKey.put(1, 2); // kty = EC2
        coseKey.put(-1, 1); // crv = P-256
        coseKey.put(-2, x);
        coseKey.put(-3, y);

        SignatureValidator sv = new SignatureValidator();
        SignatureValidator.Input input = new SignatureValidator.Input(coseKey, signature, data, -7);
        var res = sv.validate(input);
        assertThat(res.status().name()).isEqualTo("PASS");
    }

    private byte[] trimTo32(byte[] arr) {
        if (arr.length == 32) return arr;
        if (arr.length > 32) {
            // strip leading zero
            int start = arr.length - 32;
            byte[] out = new byte[32];
            System.arraycopy(arr, start, out, 0, 32);
            return out;
        }
        // pad
        byte[] out = new byte[32];
        System.arraycopy(arr, 0, out, 32 - arr.length, arr.length);
        return out;
    }
}

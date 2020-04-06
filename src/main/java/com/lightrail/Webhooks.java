package com.lightrail;

import com.lightrail.errors.NullArgumentException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;


public class Webhooks {
    public boolean verifySignature(String signatureHeader, String secret, String payload) {
        if (signatureHeader == null) {
            throw new NullArgumentException("The signatureHeader cannot be null");
        }
        if (secret == null) {
            throw new NullArgumentException("The secret cannot be null");
        }
        if (payload == null) {
            throw new NullArgumentException("The payload cannot be null");
        }

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Hex.encodeHexString(sha256_HMAC.doFinal(payload.getBytes()));
            String[] signatureHeaders = signatureHeader.split(",");
            boolean validSignature = false;
            for (int i = 0; i < signatureHeaders.length; i++ ) {
                validSignature |= MessageDigest.isEqual(signatureHeaders[i].getBytes(), hash.getBytes());
            }

            return validSignature;
        }
        catch (Exception e){
            System.out.println("Error occurred validating signature." + e.getMessage());
        }

        return false;
    }
}

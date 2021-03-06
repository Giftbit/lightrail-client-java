package com.lightrail;

import com.lightrail.errors.LightrailConfigurationException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.utils.HexUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;


public class Webhooks {

    private final LightrailClient lr;

    public Webhooks(LightrailClient lr) {
        this.lr = lr;
    }

    public boolean verifySignature(String signatureHeader, String payload) {
        String webhookSecret = lr.getWebhookSecret();
        if (webhookSecret == null) {
            throw new LightrailConfigurationException("Webhook secret is empty");
        }
        return verifySignature(signatureHeader, payload, webhookSecret);
    }

    public boolean verifySignature(String signatureHeader, String payload, String webhookSecret) {
        if (signatureHeader == null) {
            throw new NullArgumentException("The signatureHeader cannot be null");
        }
        if (webhookSecret == null) {
            throw new NullArgumentException("The webhookSecret cannot be null");
        }
        if (payload == null) {
            throw new NullArgumentException("The payload cannot be null");
        }

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = HexUtil.convertBytesToHex(sha256_HMAC.doFinal(payload.getBytes()));
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

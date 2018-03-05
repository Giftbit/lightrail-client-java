package com.lightrail;

import com.lightrail.model.LightrailException;

public class LightrailClient {
    public static String apiKey;
    public static String sharedSecret;

    public LightrailClient(String apiKey, String sharedSecret) throws LightrailException {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        verifyApiKey();
        verifySharedSecret();
    }

    public static void verifyApiKey() throws LightrailException {
        if (apiKey == null) {
            throw new LightrailException("API key is not set");
        }
        if ("".equals(apiKey)) {
            throw new LightrailException("API key is empty");
        }
    }

    public static void verifySharedSecret() throws LightrailException {
        if (sharedSecret == null) {
            throw new LightrailException("Shared secret is not set");
        }
        if ("".equals(sharedSecret)) {
            throw new LightrailException("Shared secret is empty");
        }
    }

//    public static String generateShopperToken(Contact contact) throws LightrailException {
//        verifyApiKey();
//        verifySharedSecret();
//    } // todo write fn
}

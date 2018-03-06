package com.lightrail;

import com.lightrail.model.LightrailException;

public class LightrailClient {
    public String apiKey;
    public String sharedSecret;
    public final Accounts accounts;

    public LightrailClient(String apiKey, String sharedSecret) throws LightrailException {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        verifyApiKey();
        verifySharedSecret();

        this.accounts = new Accounts();
    }

    public void verifyApiKey() throws LightrailException {
        if (apiKey == null) {
            throw new LightrailException("API key is not set");
        }
        if ("".equals(apiKey)) {
            throw new LightrailException("API key is empty");
        }
    }

    public void verifySharedSecret() throws LightrailException {
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

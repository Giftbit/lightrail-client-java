package com.lightrail;

import com.google.gson.Gson;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.network.NetworkProvider;

public class LightrailClient {
    public String apiKey;
    public String sharedSecret;

    public Gson gson = new Gson();

    public NetworkProvider networkProvider;
    public final Accounts accounts;
    public final Contacts contacts;
    public final Cards cards;

    public LightrailClient(String apiKey, String sharedSecret, NetworkProvider np) throws LightrailException {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        verifyApiKey();
        verifySharedSecret();

        this.networkProvider = np;

        this.accounts = new Accounts(this);
        this.contacts = new Contacts(this);
        this.cards = new Cards(this);
    }

    public LightrailClient(String apiKey, String sharedSecret) throws LightrailException {
        this(apiKey, sharedSecret, new DefaultNetworkProvider());
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

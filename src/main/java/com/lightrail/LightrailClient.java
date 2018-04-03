package com.lightrail;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.network.NetworkProvider;
import com.lightrail.params.CreateShopperTokenParams;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LightrailClient {
    public String apiKey;
    public String sharedSecret;

    protected final Gson gson = new Gson();

    public NetworkProvider networkProvider;
    public final Accounts accounts;
    public final Contacts contacts;
    public final Cards cards;
    public final Programs programs;

    public LightrailClient(String apiKey, String sharedSecret, NetworkProvider np) throws LightrailException {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        verifyApiKey();
        verifySharedSecret();

        this.networkProvider = np;

        this.accounts = new Accounts(this);
        this.contacts = new Contacts(this);
        this.cards = new Cards(this);
        this.programs = new Programs(this);
    }

    public LightrailClient(String apiKey, String sharedSecret) throws LightrailException {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        verifyApiKey();
        verifySharedSecret();

        this.networkProvider = new DefaultNetworkProvider(this);
        this.accounts = new Accounts(this);
        this.contacts = new Contacts(this);
        this.cards = new Cards(this);
        this.programs = new Programs(this);
    }

    protected void verifyApiKey() throws LightrailException {
        if (apiKey == null) {
            throw new LightrailException("API key is not set");
        }
        if ("".equals(apiKey)) {
            throw new LightrailException("API key is empty");
        }
    }

    protected void verifySharedSecret() throws LightrailException {
        if (sharedSecret == null) {
            throw new LightrailException("Shared secret is not set");
        }
        if ("".equals(sharedSecret)) {
            throw new LightrailException("Shared secret is empty");
        }
    }

    public String generateShopperToken(CreateShopperTokenParams params) throws LightrailException, UnsupportedEncodingException {
        verifyApiKey();
        verifySharedSecret();
        if (params.contactId == null || params.shopperId == null || params.contactUserSuppliedId == null) {
            throw new LightrailException("Must set one of contactId, shopperId, or contactUserSuppliedId");
        }

        int validityInSeconds = 43200;
        if (params.validityInSeconds <= 0) {
            throw new LightrailException("validityInSeconds must be greater than 0");
        } else if (params.validityInSeconds > 0) {
            validityInSeconds = params.validityInSeconds;
        }


        JwtBuilder builder = Jwts.builder();


        String payload = apiKey.substring(apiKey.indexOf(".") + 1);
        payload = payload.substring(0, payload.indexOf("."));

        payload = new String(DatatypeConverter.parseBase64Binary(payload), "UTF-8");
        JsonObject jsonPayload = gson.fromJson(payload, JsonObject.class);

        if (jsonPayload.get("g") == null || jsonPayload.get("g").getAsJsonObject().get("gui") == null) {
            throw new LightrailException("Lightrail API key is invalid");
        }

        Map<String, Object> g = new HashMap<>();
        g.put("gui", jsonPayload.get("g").getAsJsonObject().get("gui").getAsString());
        g.put("gmi", jsonPayload.get("g").getAsJsonObject().get("gmi").getAsString());


        if (params.contactId != null) {
            g.put("coi", params.contactId);
        }
        if (params.shopperId != null) {
            g.put("shi", params.shopperId);
        }
        if (params.contactUserSuppliedId != null) {
            g.put("cui", params.contactUserSuppliedId);
        }

        int iat = (int) (System.currentTimeMillis() / 1000);
        int exp = iat + validityInSeconds;

        if (params.metadata != null) {
            g.put("metadata", params.metadata);
        }

        String[] roles = {"shopper"};

        Map<String, Object> claims = new HashMap<>();
        claims.put("g", g);
        claims.put("iat", iat);
        claims.put("exp", exp);
        claims.put("iss", "MERCHANT");
        claims.put("roles", roles);

        return builder.setClaims(claims)
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, sharedSecret.getBytes("UTF-8"))
                .compact();
    }

    public String urlEncode(String string) throws LightrailException {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new LightrailException("Could not URL-encode the parameter " + string);
        }
    }

}

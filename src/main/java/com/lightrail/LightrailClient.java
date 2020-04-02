package com.lightrail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lightrail.errors.LightrailConfigurationException;
import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.network.NetworkProvider;
import com.lightrail.params.GenerateShopperTokenParams;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LightrailClient {

    private String apiKey;
    private String sharedSecret;
    protected NetworkProvider networkProvider = new DefaultNetworkProvider(this);

    public final Currencies currencies = new Currencies(this);
    public final Contacts contacts = new Contacts(this);
    public final Programs programs = new Programs(this);
    public final Transactions transactions = new Transactions(this);
    public final Values values = new Values(this);
    public final Webhooks webhooks = new Webhooks();

    public LightrailClient() {
    }

    public LightrailClient(String apiKey) {
        setApiKey(apiKey);
    }

    public LightrailClient(String apiKey, String sharedSecret) {
        setApiKey(apiKey);
        setSharedSecret(sharedSecret);
    }

    public String getApiKey() {
        return apiKey;
    }

    public LightrailClient setApiKey(String apiKey) {
        NullArgumentException.check(apiKey, "apiKey");
        if (apiKey.isEmpty()) {
            throw new LightrailConfigurationException("API key is empty");
        }
        this.apiKey = apiKey;
        return this;
    }

    protected void verifyApiKey() throws LightrailRestException {
        if (apiKey == null) {
            throw new LightrailConfigurationException("API key is not set");
        }
        if (apiKey.isEmpty()) {
            throw new LightrailConfigurationException("API key is empty");
        }
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public LightrailClient setSharedSecret(String sharedSecret) {
        NullArgumentException.check(sharedSecret, "sharedSecret");
        if (sharedSecret.isEmpty()) {
            throw new LightrailConfigurationException("Shared secret is empty");
        }
        this.sharedSecret = sharedSecret;
        return this;
    }

    protected void verifySharedSecret() throws LightrailRestException {
        if (sharedSecret == null) {
            throw new LightrailConfigurationException("Shared secret is not set");
        }
        if ("".equals(sharedSecret)) {
            throw new LightrailConfigurationException("Shared secret is empty");
        }
    }

    public NetworkProvider getNetworkProvider() {
        return networkProvider;
    }

    public LightrailClient setNetworkProvider(NetworkProvider networkProvider) {
        NullArgumentException.check(networkProvider, "networkProvider");
        this.networkProvider = networkProvider;
        return this;
    }

    public String generateShopperToken(GenerateShopperTokenParams params) throws LightrailRestException, UnsupportedEncodingException {
        verifyApiKey();
        verifySharedSecret();

        NullArgumentException.check(params, "params");
        NullArgumentException.check(params.contactId, "params.contactId");
        if (params.validityInSeconds <= 0) {
            throw new LightrailConfigurationException("validityInSeconds must be greater than 0");
        }

        String apiKeySegment = apiKey.substring(apiKey.indexOf(".") + 1);
        apiKeySegment = apiKeySegment.substring(0, apiKeySegment.indexOf("."));
        apiKeySegment = new String(DatatypeConverter.parseBase64Binary(apiKeySegment), StandardCharsets.UTF_8);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        JsonObject merchantJwtPayload = gson.fromJson(apiKeySegment, JsonObject.class);

        if (merchantJwtPayload.get("g") == null || merchantJwtPayload.get("g").getAsJsonObject().get("gui") == null) {
            throw new LightrailConfigurationException("Lightrail API key is invalid");
        }

        Map<String, Object> g = new HashMap<>();
        g.put("gui", merchantJwtPayload.get("g").getAsJsonObject().get("gui").getAsString());
        g.put("gmi", merchantJwtPayload.get("g").getAsJsonObject().get("gmi").getAsString());
        g.put("tmi", merchantJwtPayload.get("g").getAsJsonObject().get("tmi").getAsString());
        g.put("coi", params.contactId);

        int iat = (int) (System.currentTimeMillis() / 1000);
        int exp = iat + params.validityInSeconds;

        String[] roles = {"shopper"};

        Map<String, Object> claims = new HashMap<>();
        claims.put("g", g);
        claims.put("iss", "MERCHANT");
        claims.put("iat", iat);
        claims.put("exp", exp);
        claims.put("roles", roles);

        if (params.metadata != null) {
            claims.put("metadata", params.metadata);
        }

        JwtBuilder builder = Jwts.builder();
        return builder.setClaims(claims)
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, sharedSecret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }
}

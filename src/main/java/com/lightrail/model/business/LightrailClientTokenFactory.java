package com.lightrail.model.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.model.Lightrail;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LightrailClientTokenFactory {

    public static String generate(String shopperId, Long validityInMs) throws IOException {

        JwtBuilder builder = Jwts.builder();
        String apiKey = Lightrail.apiKey;
        String secret = Lightrail.clientSecret;

        if (apiKey == null)
            throw new BadParameterException("Lightrail.apiKey is not set.");
        if (secret == null)
            throw new BadParameterException("Lightrail.clientSecret is not set.");

        String payload = apiKey.substring(apiKey.indexOf(".") + 1);
        payload = payload.substring(0, payload.indexOf("."));

        payload = new String(new BASE64Decoder().decodeBuffer(payload));
        JsonObject jsonObject = new Gson().fromJson(payload, JsonObject.class);
        String gui = jsonObject.get("g").getAsJsonObject().get("gui").getAsString();

        Map<String, Object> claims = new HashMap<String, Object>();
        Long iat = System.currentTimeMillis();
        claims.put("iat", iat);
        claims.put("shopperId", shopperId);

        Map<String, Object> gClaims = new HashMap<String, Object>();
        gClaims.put("gui", gui);
        claims.put("g", gClaims);

        if (validityInMs != null) {
            Long exp = iat + validityInMs;
            claims.put("exp", exp);
        }
        return builder.setClaims(claims)
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                .compact();
    }


}

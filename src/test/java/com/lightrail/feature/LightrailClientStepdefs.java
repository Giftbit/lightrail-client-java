package com.lightrail.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.LightrailClient;
import com.lightrail.errors.LightrailRestException;
import com.lightrail.params.GenerateShopperTokenParams;
import cucumber.api.java.en.Given;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import static com.lightrail.feature.util.model.TestHelpers.getJsonParams;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;


public class LightrailClientStepdefs {
    private JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/shopperTokenVariables.json")).getAsJsonObject();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public LightrailClientStepdefs() throws FileNotFoundException {
    }


    @Given("^the \\[(.[^\\]]*)\\] is set to a(?:n)? (?:in)?valid value \\[(.*)\\] it should (?:not )?throw an error(?:: \\[(.[^\\]]+)\\])?$")
    public void lightrail_client_invalid_api_key(String configOption, String value, String errorName) {
        String apiKey = Pattern.compile("(?i)API key").matcher(configOption).find() ? value : "anything";
        String secret = Pattern.compile("(?i)shared secret").matcher(configOption).find() ? value : "anything";

        try {
            new LightrailClient(apiKey, secret);
            if (errorName != null) {
                fail("Invalid LightrailClient config should throw an exception");
            }
        } catch (LightrailRestException ignored) {
            if (errorName == null) {
                fail("Valid LightrailClient config should not throw an exception");
            }
        }
    }

    @Given("^TOKEN_GENERATION a token should contain the contact identifier it is generated with: \\[(.*)\\] as \\[(.*)\\]$")
    public void tokenGenerationWithContactIdentifier(String contactIdentifier, String keyInJwt) throws LightrailRestException, UnsupportedEncodingException {
        LightrailClient lr = new LightrailClient("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJnIjp7Imd1aSI6Imdvb2V5IiwiZ21pIjoiZ2VybWllIn19.XxOjDsluAw5_hdf5scrLk0UBn8VlhT-3zf5ZeIkEld8", "secret");
        JsonObject jsonParams = getJsonParams(jsonVariables, contactIdentifier);
        GenerateShopperTokenParams tokenParams = gson.fromJson(jsonParams, GenerateShopperTokenParams.class);

        String token = lr.generateShopperToken(tokenParams);

        Jwt parsedToken = Jwts.parser().setSigningKey(lr.sharedSecret.getBytes("UTF-8")).parse(token);
        Claims tokenBody = (Claims) (parsedToken.getBody());
        LinkedHashMap g = (LinkedHashMap) tokenBody.get("g");
        Integer iat = (Integer) tokenBody.get("iat");
        Integer exp = (Integer) tokenBody.get("exp");
        String iss = (String) tokenBody.get("iss");

        assertEquals(jsonParams.get(contactIdentifier).getAsString(), g.get(keyInJwt));
        assertEquals("MERCHANT", iss);
    }

    @Given("^TOKEN_GENERATION a token should have the right validity period when generated with params \\[(.*)\\]$")
    public void tokenGenerationWithExpiry(String params) throws LightrailRestException, UnsupportedEncodingException {
        LightrailClient lr = new LightrailClient("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJnIjp7Imd1aSI6Imdvb2V5IiwiZ21pIjoiZ2VybWllIn19.XxOjDsluAw5_hdf5scrLk0UBn8VlhT-3zf5ZeIkEld8", "secret");
        JsonObject jsonParams = getJsonParams(jsonVariables, params);
        GenerateShopperTokenParams tokenParams = gson.fromJson(jsonParams, GenerateShopperTokenParams.class);

        String token = lr.generateShopperToken(tokenParams);

        Jwt parsedToken = Jwts.parser().setSigningKey(lr.sharedSecret.getBytes("UTF-8")).parse(token);
        Claims tokenBody = (Claims) (parsedToken.getBody());
        Integer iat = (Integer) tokenBody.get("iat");
        Integer exp = (Integer) tokenBody.get("exp");

        assertEquals(iat + tokenParams.validityInSeconds, (int) exp);
    }

    @Given("^TOKEN_GENERATION a token should contain the metadata it is generated with: \\[(.*)\\]$")
    public void tokenGenerationWithMetadata(String params) throws LightrailRestException, UnsupportedEncodingException {
        LightrailClient lr = new LightrailClient("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJnIjp7Imd1aSI6Imdvb2V5IiwiZ21pIjoiZ2VybWllIn19.XxOjDsluAw5_hdf5scrLk0UBn8VlhT-3zf5ZeIkEld8", "secret");
        JsonObject jsonParams = getJsonParams(jsonVariables, params);
        GenerateShopperTokenParams tokenParams = gson.fromJson(jsonParams, GenerateShopperTokenParams.class);


        String token = lr.generateShopperToken(tokenParams);

        Jwt parsedToken = Jwts.parser().setSigningKey(lr.sharedSecret.getBytes("UTF-8")).parse(token);
        Claims tokenBody = (Claims) (parsedToken.getBody());
        LinkedHashMap g = (LinkedHashMap) tokenBody.get("g");
        HashMap metadata = (HashMap) g.get("metadata");

        assertEquals(tokenParams.metadata, metadata);
    }
}

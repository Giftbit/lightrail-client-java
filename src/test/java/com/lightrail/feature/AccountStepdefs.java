package com.lightrail.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.network.NetworkProvider;
import com.lightrail.params.CreateAccountCardByContactIdParams;
import com.lightrail.params.CreateAccountCardByShopperIdParams;
import cucumber.api.java.en.Given;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;

public class AccountStepdefs {
    @Given("^ACCOUNT_CREATION a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void contact_does_or_does_not_exist(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {

        JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/variables.json")).getAsJsonObject();

        Map<String, JsonElement> reqResCollection = getReqResCollection(expectedRequestsAndResponses, jsonVariables);

        NetworkProvider npMock = mock(DefaultNetworkProvider.class);
        LightrailClient lr = new LightrailClient("123", "123", npMock);

        setReqResExpectations(reqResCollection, lr);

        JsonObject jsonParams = getJsonParams(minimumParams, jsonVariables);

        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            CreateAccountCardByContactIdParams minParams = lr.gson.fromJson(jsonParams.toString(), CreateAccountCardByContactIdParams.class);
            boolean exceptionThrown = false;

            try {
                lr.accounts.create(minParams);
            } catch (LightrailException e) {
                exceptionThrown = true;
            }

            if (errorName != null) {
                assertEquals(true, exceptionThrown);
            } else {
                assertEquals(false, exceptionThrown);
            }
        } else if (Pattern.compile("(?i)shopperid").matcher(minimumParams).find()) {
            CreateAccountCardByShopperIdParams minParams = lr.gson.fromJson(jsonParams.toString(), CreateAccountCardByShopperIdParams.class);
            lr.accounts.create(minParams);
        }

        verifyMock(reqResCollection, lr);
    }


    private JsonObject getJsonParams(String minimumParams, JsonObject jsonVariables) {
        String[] minParamKeys = minimumParams.split(", ");
        JsonObject miniParams = new JsonObject();
        for (int index = 0; index < minParamKeys.length; index++) {
            miniParams.add(minParamKeys[index], jsonVariables.get(minParamKeys[index]));
        }
        return miniParams;
    }

    private Map<String, JsonElement> getReqResCollection(String expectedRequestsAndResponses, JsonObject jsonVariables) {
        String[] reqResKeys = expectedRequestsAndResponses.split(", ");
        Map<String, JsonElement> reqResCollection = new HashMap<>();
        for (int index = 0; index < reqResKeys.length; index++) {
            reqResCollection.put(reqResKeys[index], jsonVariables.get("requestResponseCombos").getAsJsonObject().get(reqResKeys[index]));
        }

        return reqResCollection;
    }

    private void setReqResExpectations(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name;
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();
            String response = reqResDetails.get("response").toString();

            // todo: generate body string?

            if (Pattern.compile("(?i)error").matcher(reqResKey).find()) {
                when(lr.networkProvider.getAPIResponse(matches(lr.apiKey), contains(endpoint), matches("(?i)" + method), (String) any())).thenThrow(new LightrailException(""));  // todo: this exception needs to match the `errorName` passed in from the feature file
            } else {
                when(lr.networkProvider.getAPIResponse(matches(lr.apiKey), contains(endpoint), matches("(?i)" + method), (String) any())).thenReturn(reqResDetails.get("response").toString());
            }
        }
    }

    private void verifyMock(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();

            verify(lr.networkProvider, times(1)).getAPIResponse(matches(lr.apiKey), contains(endpoint), matches("(?i)" + method), (String) any());
        }
    }

}

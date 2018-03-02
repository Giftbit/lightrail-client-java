package com.lightrail.old.feature;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.old.exceptions.AuthorizationException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;
import com.lightrail.old.exceptions.InsufficientValueException;
import com.lightrail.old.model.Lightrail;
import com.lightrail.old.model.api.net.APICore;
import com.lightrail.old.model.api.net.DefaultNetworkProvider;
import com.lightrail.old.model.api.net.NetworkProvider;
import com.lightrail.old.model.api.objects.LightrailObject;
import com.lightrail.old.model.api.objects.RequestParamsCreateAccountByContactId;
import com.lightrail.old.model.api.objects.RequestParamsCreateAccountByShopperId;
import com.lightrail.old.model.business.AccountCard;
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
    @Given("^a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void contact_does_or_does_not_exist(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Lightrail.apiKey = "123";
        JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/variables.json")).getAsJsonObject();

        Map<String, JsonElement> reqResCollection = getReqResCollection(expectedRequestsAndResponses, jsonVariables);

        NetworkProvider npMock = mock(DefaultNetworkProvider.class);
        APICore.setNetworkProvider(npMock);

        setReqResExpectations(reqResCollection, npMock);

        LightrailObject minParams = null;
        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            JsonObject jsonParams = getJsonParams(minimumParams, jsonVariables);
            minParams = new Gson().fromJson(jsonParams.toString(), RequestParamsCreateAccountByContactId.class);
            boolean exceptionThrown = false;
            try {
                AccountCard.create((RequestParamsCreateAccountByContactId) minParams);
            } catch (CouldNotFindObjectException ignored) {
                exceptionThrown = true;
            }
            if (errorName != null) {
                assertEquals(true, exceptionThrown);
            }
        } else if (Pattern.compile("(?i)shopperid").matcher(minimumParams).find()) {
            JsonObject jsonParams = getJsonParams(minimumParams, jsonVariables);
            minParams = new Gson().fromJson(jsonParams.toString(), RequestParamsCreateAccountByShopperId.class);
            AccountCard.create((RequestParamsCreateAccountByShopperId) minParams);
        }

        verifyMock(reqResCollection, npMock);
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

    private void setReqResExpectations(Map<String, JsonElement> reqResCollection, NetworkProvider npMock) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();
            String response = reqResDetails.get("response").toString();

            // todo: generate body string?

            if (Pattern.compile("(?i)error").matcher(reqResKey).find()) {
                when(npMock.getRawAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenThrow(new CouldNotFindObjectException(""));  // todo: this exception needs to match the `errorName` passed in from the feature file
            } else {
                when(npMock.getRawAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenReturn(reqResDetails.get("response").toString());
            }
        }
    }

    private void verifyMock(Map<String, JsonElement> reqResCollection, NetworkProvider npMock) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();

            verify(npMock, times(1)).getRawAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any());
        }
    }

}
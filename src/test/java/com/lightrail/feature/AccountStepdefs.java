package com.lightrail.feature;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.params.CreateAccountCardByContactIdParams;
import com.lightrail.params.CreateAccountCardByShopperIdParams;
import com.lightrail.params.CreateAccountTransactionByContactIdParams;
import com.lightrail.params.CreateAccountTransactionByShopperIdParams;
import cucumber.api.java.en.Given;

import java.io.FileNotFoundException;
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
    private JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/variables.json")).getAsJsonObject();
    private DefaultNetworkProvider npMock = mock(DefaultNetworkProvider.class);
    private LightrailClient lr = new LightrailClient("123", "123", npMock);
    private Gson gson = new Gson();

    public AccountStepdefs() throws LightrailException, FileNotFoundException {
    }

    @Given("^ACCOUNT_CREATION a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void accountCreation(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Map<String, JsonElement> reqResCollection = getReqResCollection(expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(minimumParams);

        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            CreateAccountCardByContactIdParams minParams = gson.fromJson(jsonParams.toString(), CreateAccountCardByContactIdParams.class);
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
            CreateAccountCardByShopperIdParams minParams = gson.fromJson(jsonParams.toString(), CreateAccountCardByShopperIdParams.class);
            lr.accounts.create(minParams);
        }

        verifyMock(reqResCollection, lr);
    }

    @Given("^ACCOUNT_RETRIEVAL a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void accountRetrieval(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Map<String, JsonElement> reqResCollection = getReqResCollection(expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(minimumParams);

        String currency = jsonParams.get("currency").getAsString();

        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            String contactId = jsonParams.get("contactId").getAsString();
            lr.accounts.retrieveByContactIdAndCurrency(contactId, currency);
        } else if (Pattern.compile("(?i)shopperid").matcher(minimumParams).find()) {
            String shopperId = jsonParams.get("shopperId").getAsString();
            lr.accounts.retrieveByShopperIdAndCurrency(shopperId, currency);
        }

        verifyMock(reqResCollection, lr);
    }


    @Given("^ACCOUNT_TRANSACTION a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void accountTransaction(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Map<String, JsonElement> reqResCollection = getReqResCollection(expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(minimumParams);

        String currency = jsonParams.get("currency").getAsString();

        boolean exceptionThrown = false;
        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            CreateAccountTransactionByContactIdParams minParams = gson.fromJson(jsonParams.toString(), CreateAccountTransactionByContactIdParams.class);

            String contactId = jsonParams.get("contactId").getAsString();

            try {
                lr.accounts.createTransaction(minParams);
            } catch (LightrailException e) {
                exceptionThrown = true;
            }
        } else if (Pattern.compile("(?i)shopperid").matcher(minimumParams).find()) {
            CreateAccountTransactionByShopperIdParams minParams = gson.fromJson(jsonParams.toString(), CreateAccountTransactionByShopperIdParams.class);

            String shopperId = jsonParams.get("shopperId").getAsString();

            try {
                lr.accounts.createTransaction(minParams);
            } catch (LightrailException e) {
                exceptionThrown = true;
            }
        }

        if (errorName != null) {
            assertEquals(true, exceptionThrown);
        } else {
            assertEquals(false, exceptionThrown);
        }

        verifyMock(reqResCollection, lr);
    }


    private JsonObject getJsonParams(String minimumParams) {
        String[] minParamKeys = minimumParams.split(", ");
        JsonObject miniParams = new JsonObject();
        for (int index = 0; index < minParamKeys.length; index++) {
            miniParams.add(minParamKeys[index], jsonVariables.get(minParamKeys[index]));
        }
        return miniParams;
    }

    private Map<String, JsonElement> getReqResCollection(String expectedRequestsAndResponses) {
        String[] reqResKeys = expectedRequestsAndResponses.split(", ");
        Map<String, JsonElement> reqResCollection = new HashMap<>();
        for (int index = 0; index < reqResKeys.length; index++) {
            reqResCollection.put(reqResKeys[index], jsonVariables.get("requestResponseCombos").getAsJsonObject().get(reqResKeys[index]));
        }

        return reqResCollection;
    }

    private void setReqResExpectations(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        reset(lr.networkProvider);

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

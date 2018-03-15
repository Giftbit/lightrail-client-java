package com.lightrail.feature;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.params.*;
import cucumber.api.java.en.Given;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.regex.Pattern;

import static com.lightrail.feature.util.model.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AccountStepdefs {
    private JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/accountVariables.json")).getAsJsonObject();
    private DefaultNetworkProvider npMock = mock(DefaultNetworkProvider.class);
    private LightrailClient lr = new LightrailClient("123", "123", npMock);
    private Gson gson = new Gson();

    public AccountStepdefs() throws LightrailException, FileNotFoundException {
    }

    @Given("^ACCOUNT_CREATION a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void accountCreation(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, minimumParams);

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
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, minimumParams);

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
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, minimumParams);

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

    @Given("^ACCOUNT_TRANSACTION a pending transaction exists: \\[(.[^\\]]+)\\] requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\]$")
    public void accountTransactionVoidOrCapturePending(String actionOnPending, String minimumParams, String expectedRequestsAndResponses) throws Throwable {
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, minimumParams);


        if (Pattern.compile("(?i)contactid").matcher(minimumParams).find()) {
            HandleAccountPendingByContactId minParams = gson.fromJson(jsonParams.toString(), HandleAccountPendingByContactId.class);
            if (Pattern.compile("(?i)capture").matcher(actionOnPending).find()) {
                lr.accounts.capturePendingTransaction(minParams);
            } else if (Pattern.compile("(?i)void").matcher(actionOnPending).find()) {
                lr.accounts.voidPendingTransaction(minParams);
            }

        } else if (Pattern.compile("(?i)shopperid").matcher(minimumParams).find()) {
            HandleAccountPendingByShopperId minParams = gson.fromJson(jsonParams.toString(), HandleAccountPendingByShopperId.class);
            if (Pattern.compile("(?i)capture").matcher(actionOnPending).find()) {
                lr.accounts.capturePendingTransaction(minParams);
            } else if (Pattern.compile("(?i)void").matcher(actionOnPending).find()) {
                lr.accounts.voidPendingTransaction(minParams);
            }
        }

        verifyMock(reqResCollection, lr);
    }
}

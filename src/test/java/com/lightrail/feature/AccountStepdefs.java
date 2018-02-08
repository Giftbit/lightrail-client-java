package com.lightrail.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.net.DefaultNetworkProvider;
import com.lightrail.model.api.net.NetworkProvider;
import com.lightrail.model.business.LightrailContact;
import cucumber.api.java.en.Given;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AccountStepdefs {
    @Given("^a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void contact_does_or_does_not_exist(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        Lightrail.apiKey = "123";
        JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/variables.json")).getAsJsonObject();

        String[] minParamKeys = minimumParams.split(", ");
        Map<String, JsonElement> minParams = new HashMap<>();
        for (int index = 0; index < minParamKeys.length; index++) {
            minParams.put(minParamKeys[index], jsonVariables.get(minParamKeys[index]));
        }

        String[] reqResKeys = expectedRequestsAndResponses.split(", ");
        Map<String, JsonElement> reqResCollection = new HashMap<>();
        for (int index = 0; index < reqResKeys.length; index++) {
            reqResCollection.put(reqResKeys[index], jsonVariables.get("requestResponseCombos").getAsJsonObject().get(reqResKeys[index]));
        }

        NetworkProvider npMock = mock(DefaultNetworkProvider.class);//, new DefaultJsonAnswer());
        APICore.setNetworkProvider(npMock);


        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();
            String response = reqResDetails.get("response").toString();

            System.out.println("SETTING UP MOCK EXPECTATION for: " + reqResKey + "  " + endpoint + "  " + response);

            if (Pattern.compile("(?i)error").matcher(reqResKey).find()) {
                when(npMock.getRawAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenThrow(new CouldNotFindObjectException(""));
//            } else if (Pattern.compile("(?i)contact").matcher(reqResKey).find()) {
//                when(npMock.getRawAPIResponse(contains("contacts"), matches("(?i)" + "get"), (String) any())).thenReturn("{\"contact\":{\"contactId\":\"contact-12345\"}}");
//            } else if (Pattern.compile("(?i)card").matcher(reqResKey).find()) {
//                when(npMock.getRawAPIResponse(contains("cards"), matches("(?i)" + "get"), (String) any())).thenReturn("{\"cards\":[]}");
//            }
            } else {
                when(npMock.getRawAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenReturn(reqResDetails.get("response").toString());
//                when(npMock.getRawAPIResponse((String) any(), (String) any(), (String) any())).thenReturn(reqResDetails.get("response").toString());
            }
        }

        // todo: generate body string?

        try {
            LightrailContact.retrieve(minParams.get("contactId").getAsString());//.addCurrency(minParams.get("currency").getAsString());
        } catch (CouldNotFindObjectException e) {
            assertEquals(e.getMessage(), "");
        }
    }

}
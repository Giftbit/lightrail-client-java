package com.lightrail.feature.util.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

public class TestHelpers {
    public static JsonObject getJsonParams(JsonObject jsonVariables, String minimumParams) {
        String[] minParamKeys = minimumParams.split(", ");
        JsonObject miniParams = new JsonObject();
        for (int index = 0; index < minParamKeys.length; index++) {
            miniParams.add(minParamKeys[index], jsonVariables.get(minParamKeys[index]));
        }
        return miniParams;
    }

    public static Map<String, JsonElement> getReqResCollection(JsonObject jsonVariables, String expectedRequestsAndResponses) {
        String[] reqResKeys = expectedRequestsAndResponses.split(", ");
        Map<String, JsonElement> reqResCollection = new HashMap<>();
        for (int index = 0; index < reqResKeys.length; index++) {
            reqResCollection.put(reqResKeys[index], jsonVariables.get("requestResponseCombos").getAsJsonObject().get(reqResKeys[index]));
        }

        return reqResCollection;
    }

    public static void setReqResExpectations(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        reset(lr.networkProvider);

        for (String reqResKey : reqResCollection.keySet()) {
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();
            String response = reqResDetails.get("response").toString();

            // todo: generate body string?

            boolean expectingError = Pattern.compile("(?i)error").matcher(reqResKey).find();

            if (Pattern.compile("(?i)get").matcher(method).find()) {
                setGetExpectation(endpoint, response, expectingError, lr);
            } else if (Pattern.compile("(?i)post").matcher(method).find()) {
                setPostExpectation(endpoint, response, expectingError, lr);
            }
        }
    }

    private static void setGetExpectation(String endpoint, String response, boolean expectError, LightrailClient lr) throws LightrailException {
        if (expectError) {
            when(lr.networkProvider.get(contains(endpoint))).thenThrow(new LightrailException(""));   // todo better exception matching
        } else {
            when(lr.networkProvider.get(contains(endpoint))).thenReturn(response);
        }

    }

    private static void setPostExpectation(String endpoint, String response, boolean expectError, LightrailClient lr) throws LightrailException {
        // todo needs refactoring to check request body: second arg matcher should be motivated, not 'any()'
        if (expectError) {
            when(lr.networkProvider.post(contains(endpoint), (String) any())).thenThrow(new LightrailException(""));   // todo better exception matching
        } else {
            when(lr.networkProvider.post(contains(endpoint), (String) any())).thenReturn(response);
        }
    }

    public static void verifyMock(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();

            if (Pattern.compile("(?i)get").matcher(method).find()) {
                verify(lr.networkProvider, times(1)).get(contains(endpoint));
            }
            if (Pattern.compile("(?i)post").matcher(method).find()) {
                verify(lr.networkProvider, times(1)).post(contains(endpoint), (String) any());
            }


        }
    }
}

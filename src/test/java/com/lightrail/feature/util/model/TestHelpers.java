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
import static org.mockito.ArgumentMatchers.matches;
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

            if (Pattern.compile("(?i)error").matcher(reqResKey).find()) {
                when(lr.networkProvider.getAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenThrow(new LightrailException(""));  // todo: this exception needs to match the `errorName` passed in from the feature file
            } else {
                when(lr.networkProvider.getAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any())).thenReturn(reqResDetails.get("response").toString());
            }
        }
    }

    public static void verifyMock(Map<String, JsonElement> reqResCollection, LightrailClient lr) throws IOException, LightrailException {
        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

            String endpoint = reqResDetails.get("endpoint").getAsString();
            String method = reqResDetails.get("httpMethod").getAsString();

            verify(lr.networkProvider, times(1)).getAPIResponse(contains(endpoint), matches("(?i)" + method), (String) any());
        }
    }
}

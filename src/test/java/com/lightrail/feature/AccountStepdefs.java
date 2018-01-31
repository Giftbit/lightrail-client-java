package com.lightrail.feature;

import com.google.gson.*;
import com.google.gson.JsonObject;
import cucumber.api.java.en.Given;

import java.io.FileReader;
import java.util.*;


public class AccountStepdefs {
    @Given("^a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void contact_does_or_does_not_exist(String minimumParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/java/com/lightrail/feature/variables.json")).getAsJsonObject();

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


        System.out.println("LOOPING OVER REQRES:");


        for (String name : reqResCollection.keySet()) {
            String reqResKey = name.toString();
            JsonObject reqResDetails = reqResCollection.get(reqResKey).getAsJsonObject();

//            if /error/ regex matches reqResKey
//              expect to make call and receive error:
//                  method: reqResDetails.get("httpMethod")
//                  endpoint: reqResDetails.get("endpoint")
//                  reqResDetails: reqResDetails.get("httpMethod")
//                  errorName: errorName
//            else
//              expect to make call and receive response
//            end

            System.out.println("method: " + reqResDetails.get("httpMethod") + " endpoint: " + reqResDetails.get("endpoint") + " response: " + reqResDetails.get("response"));
        }
    }
}
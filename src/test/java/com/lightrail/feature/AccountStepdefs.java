package com.lightrail.feature;

import com.google.gson.*;
import cucumber.api.java.en.Given;

import java.io.FileReader;


public class AccountStepdefs {
    @Given("^a contact .*\\s*exists?\\s*.*: requires minimum parameters \\[(.[^\\]]+)\\] and makes the following REST requests: \\[(.[^\\]]+)\\](?: and throws the following error: \\[(.[^\\]]+)\\])?$")
    public void contact_does_or_does_not_exist(String minParams, String expectedRequestsAndResponses, String errorName) throws Throwable {
        System.out.println(minParams);
        System.out.println(expectedRequestsAndResponses);
        System.out.println(errorName);

        JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/java/com/lightrail/feature/variables.json")).getAsJsonObject();
        System.out.println(jsonVariables.get("shopperId"));
    }
}
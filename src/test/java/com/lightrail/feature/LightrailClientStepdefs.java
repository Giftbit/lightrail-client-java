package com.lightrail.feature;

import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import cucumber.api.java.en.Given;

import java.util.regex.Pattern;

import static junit.framework.TestCase.fail;


public class LightrailClientStepdefs {
    @Given("^the \\[(.[^\\]]*)\\] is set to a(?:n)? (?:in)?valid value \\[(.*)\\] it should (?:not )?throw an error(?:: \\[(.[^\\]]+)\\])?$")
    public void lightrail_client_invalid_api_key(String configOption, String value, String errorName) throws Throwable {
        String apiKey = Pattern.compile("(?i)API key").matcher(configOption).find() ? value : "anything";
        String secret = Pattern.compile("(?i)shared secret").matcher(configOption).find() ? value : "anything";

        try {
            new LightrailClient(apiKey, secret);
            if (errorName != null) {
                fail("Invalid LightrailClient config should throw an exception");
            }
        } catch (LightrailException ignored) {
            if (errorName == null) {
                fail("Valid LightrailClient config should not throw an exception");
            }
        }
    }
}
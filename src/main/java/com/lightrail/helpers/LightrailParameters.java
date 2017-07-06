package com.lightrail.helpers;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.model.Lightrail;

import java.util.List;
import java.util.Map;

public class LightrailParameters {

    public static String CURRENCY = "currency";
    public static String CODE = "code";
    public static String AMOUNT = "amount";
    public static String VALUE = "value";
    public static String USER_SUPPLIED_ID = "userSuppliedId";
    public static String CAPTURE = "capture";
    public static String PENDING = "pending";


    public static void requireParameters (List<String> requiredParams, Map<String, Object> givenParams) throws BadParameterException {
        if (Lightrail.apiKey == null)
            throw new BadParameterException("API Key is not set. You can set the API key as follows: 'Lightrail.apiKey=...'");
        for (String paramName : requiredParams) {
            if (! givenParams.containsKey(paramName) || givenParams.get(paramName) == null)
                throw new BadParameterException(String.format("Missing Parameter: %s.", paramName));
        }

    }
}

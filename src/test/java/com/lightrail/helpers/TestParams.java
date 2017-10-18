package com.lightrail.helpers;

import com.lightrail.model.api.objects.RequestParameters;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestParams {

    public static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("_test-config.properties"));
        return properties;
    }

    public static RequestParameters readCodeParamsFromProperties () throws IOException {
        Properties properties = getProperties();

        RequestParameters params = new RequestParameters();
        params.put("code", properties.getProperty("happyPath.code"));
        params.put("currency", properties.getProperty("happyPath.code.currency"));

        return params;
    }

    public static RequestParameters readCardParamsFromProperties () throws IOException {
        Properties properties = getProperties();

        RequestParameters params = new RequestParameters();
        params.put("cardId", properties.getProperty("happyPath.code.cardId"));
        params.put("currency", properties.getProperty("happyPath.code.currency"));

        return params;
    }
}

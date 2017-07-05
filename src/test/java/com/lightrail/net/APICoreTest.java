package com.lightrail.net;

import com.google.gson.Gson;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 */
public class APICoreTest {

    public static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("_test-config.properties"));
        return properties;
    }

    @Test
    public void pingTest() throws IOException {
        Properties properties = getProperties();
        Lightrail.apiKey = properties.getProperty("testApiKey");
        System.out.println(new Gson().toJson(APICore.ping()));
    }

    @Test
    public void balanceCheckTest() throws IOException {
        Properties properties = getProperties();
        Lightrail.apiKey = properties.getProperty("testApiKey");
        System.out.println(new Gson().toJson(APICore.balanceCheck(properties.getProperty("happyPath.code"))));
    }

    @Test
    public void withdrawFromCodeTest() throws IOException {
        Properties properties = getProperties();
        Lightrail.apiKey = properties.getProperty("testApiKey");

        APICore.postTransactionOnCode(
                properties.getProperty("happyPath.code"),
                -101,
                properties.getProperty("happyPath.code.currency"),
                "hah0000000001");
    }
}

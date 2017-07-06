package com.lightrail.net;

import com.google.gson.Gson;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.Transaction;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        System.out.println(new Gson().toJson(APICore.ping()));
    }

    @Test
    public void balanceCheckTest() throws IOException {
        Properties properties = getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        System.out.println(new Gson().toJson(APICore.balanceCheck(properties.getProperty("happyPath.code"))));
    }

    @Test
    public void withdrawFromCodeTest() throws IOException {
        Properties properties = getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> transactionParams = new HashMap<String, Object>();
        transactionParams.put("value", -101);
        transactionParams.put("currency", properties.getProperty("happyPath.code.currency"));
        transactionParams.put("userSuppliedId", "hah0000000001");

        Transaction codeTransaction = APICore.postTransactionOnCode(properties.getProperty("happyPath.code"), transactionParams);
        System.out.println();
    }
}

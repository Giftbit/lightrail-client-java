package com.lightrail.model.business;

import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.net.APICoreTest;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftValueTest {

    @Test
    public void GifValueRetrieveHappyPath() throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, BadParameterException {
        Properties properties = APICoreTest.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = new HashMap<String, Object>();
        giftValueParams.put("code", properties.getProperty("happyPath.code"));
        giftValueParams.put("currency", properties.getProperty("happyPath.code.currency"));

        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        assertEquals(Integer.parseInt(properties.getProperty("happyPath.code.value")), giftValue.getCurrentValue());
        assertEquals(properties.getProperty("happyPath.code.currency"), giftValue.getCurrency());
    }

    @Test
    public void GiftValueRetrieveMissingParametersTest() throws IOException, CurrencyMismatchException {
        Properties properties = APICoreTest.getProperties();

        Map<String, Object> giftValueParams = new HashMap<>();
        try {
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(e.getClass().getName(), BadParameterException.class.getName());
        }

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        giftValueParams.put("code", null);
        try {
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(e.getClass().getName(), BadParameterException.class.getName());
        }

        giftValueParams.put("code", properties.getProperty("happyPath.code"));
        try {
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(e.getClass().getName(), BadParameterException.class.getName());
        }

        giftValueParams = new HashMap<>();
        giftValueParams.put("currency", properties.getProperty("happyPath.code.currency"));

        try {
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(e.getClass().getName(), BadParameterException.class.getName());
        }

    }

}

package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftValueTest {

    @Test
    public void GifValueRetrieveByCodeHappyPath() throws IOException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        GiftValue giftValue = GiftValue.retrieveByCode(properties.getProperty("happyPath.code"));
    }

    @Test
    public void GifValueRetrieveByCardHappyPath() throws IOException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        GiftValue giftValueByCardId = GiftValue.retrieveByCardId(properties.getProperty("happyPath.code.cardId"));
        GiftValue giftValueByCode = GiftValue.retrieveByCode(properties.getProperty("happyPath.code"));

        assertEquals(giftValueByCode.getCurrentValue(), giftValueByCardId.getCurrentValue());
    }

    @Test
    public void GifValueByCodeWithCurrencyRetrieveHappyPath() throws IOException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);
    }

    @Test
    public void GifValueByCardIdWithCurrencyRetrieveHappyPath() throws IOException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCardParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);
    }

    @Test
    public void NoAuthorizationCase() throws IOException, AuthorizationException, CurrencyMismatchException {
        Lightrail.apiKey = "";

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        try {
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(AuthorizationException.class.getName(), e.getClass().getName());
        }
    }

    @Test
    public void CurrencyMismatchCase() throws IOException, AuthorizationException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        try {
            String wrongCurrency = "CAD";
            if ("CAD".equals(properties.getProperty("happyPath.code.currency")))
                wrongCurrency = "USD";

            giftValueParams.put("currency", wrongCurrency);
            GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        } catch (Exception e) {
            assertEquals(CurrencyMismatchException.class.getName(), e.getClass().getName());
        }
    }

    @Test
    public void GiftValueRetrieveMissingParametersTest() throws IOException, CurrencyMismatchException {
        Properties properties = TestParams.getProperties();

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

package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftChargeTest {

    @Test
    public void GiftChargeCapturedCreateHappyPath () throws IOException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put("amount", chargeAmount);

        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeAuthCancelHappyPath () throws IOException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put("amount", chargeAmount);
        giftChargeParams.put("capture", false);

        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());

        giftCharge.cancel();
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeAuthCaptureHappyPath () throws IOException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put("amount", chargeAmount);
        giftChargeParams.put("capture", false);

        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());

        giftCharge.capture();
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

}

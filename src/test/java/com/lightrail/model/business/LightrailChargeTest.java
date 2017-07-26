package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class LightrailChargeTest {

    @Test
    public void GiftChargeByCodeCapturedCreateHappyPath () throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, chargeAmount);

        LightrailCharge giftCharge = LightrailCharge.create(giftChargeParams);

        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeByCodeCapturedCreateHappyPathWithoutParams () throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        LightrailCharge giftCharge = LightrailCharge.createByCode(
                properties.getProperty("happyPath.code"),
                chargeAmount,
                properties.getProperty("happyPath.code.currency"));
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeByCardCapturedCreateHappyPathWithoutParams () throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        LightrailCharge giftCharge = LightrailCharge.createByCardId(
                properties.getProperty("happyPath.code.cardId"),
                chargeAmount,
                properties.getProperty("happyPath.code.currency"));
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeAuthCancelHappyPath () throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, chargeAmount);
        giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, false);

        LightrailCharge giftCharge = LightrailCharge.create(giftChargeParams);
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());

        giftCharge.cancel();
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeAuthCaptureHappyPath () throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, chargeAmount);
        giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, false);

        LightrailCharge giftCharge = LightrailCharge.create(giftChargeParams);
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());

        giftCharge.capture();
        assertEquals(chargeAmount, giftCharge.getAmount());
        assertEquals(properties.getProperty("happyPath.code.cardId"), giftCharge.getCardId());
    }

    @Test
    public void GiftChargeInsufficientValueTest() throws IOException, AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        LightrailValue giftValue = LightrailValue.retrieve(giftValueParams);
        int giftCodeValue = giftValue.getCurrentValue();

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, giftCodeValue + 1);
        try {
            LightrailCharge giftCharge = LightrailCharge.create(giftChargeParams);
        } catch (Exception e) {
            assertEquals(InsufficientValueException.class.getName(), e.getClass().getName());
        }
    }

    @Test
    public void GiftChargeIdempotencyTest () throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int chargeAmount = 11;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, chargeAmount);

        LightrailCharge giftCharge = LightrailCharge.create(giftChargeParams);
        String idempotencyKey = giftCharge.getIdempotencyKey();
        String firstTransactionId = giftCharge.getTransactionId();

        giftChargeParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, idempotencyKey);
        giftCharge = LightrailCharge.create(giftChargeParams);

        String secondTransactionId = giftCharge.getTransactionId();

        assertEquals(firstTransactionId, secondTransactionId);
    }
}

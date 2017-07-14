package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    @Test
    public void GiftValueChargeCancelTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, chargeAmount);
        giftChargeParams.put(Constants.LightrailParameters.CAPTURE, false);

        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        int newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        assertEquals(giftCodeValue - chargeAmount, newGiftCodeValue);

        giftCharge.cancel();
        newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();
        assertEquals(giftCodeValue, newGiftCodeValue);
    }

    @Test
    public void GiftValueFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        int valueAdded = 101;

        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put(Constants.LightrailParameters.AMOUNT, valueAdded);
        GiftFund giftFund = GiftFund.create(giftFundParams);

        int newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        assertEquals(giftCodeValue + valueAdded, newGiftCodeValue);
    }

    @Test
    public void GiftValueChargeCaptureFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, chargeAmount);
        giftChargeParams.put(Constants.LightrailParameters.CAPTURE, false);
        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

        int newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();
        assertEquals(giftCodeValue - chargeAmount, newGiftCodeValue);
        giftCharge.capture();
        newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();
        assertEquals(giftCodeValue - chargeAmount, newGiftCodeValue);

        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put(Constants.LightrailParameters.AMOUNT, chargeAmount);

        GiftFund giftFund = GiftFund.create(giftFundParams);
        newGiftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();
        assertEquals(giftCodeValue, newGiftCodeValue);

    }

    @Test
    public void GiftValueChargeFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        int giftCodeValue = giftValue.getCurrentValue();

        int chargeAmount = 101;

        Map<String, Object> giftChargeParams = TestParams.readCodeParamsFromProperties();
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, chargeAmount);
        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

        GiftValue newGiftValue = GiftValue.retrieve(giftValueParams);
        int newGiftCodeValue = newGiftValue.getCurrentValue();

        assertEquals(giftCodeValue - chargeAmount, newGiftCodeValue);

        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put(Constants.LightrailParameters.AMOUNT, chargeAmount);

        GiftFund giftFund = GiftFund.create(giftFundParams);

        newGiftValue = GiftValue.retrieve(giftValueParams);
        newGiftCodeValue = newGiftValue.getCurrentValue();
        assertEquals(giftCodeValue, newGiftCodeValue);
    }

}

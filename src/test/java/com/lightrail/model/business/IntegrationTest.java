package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    @Test
    public void GiftValueChargeCancelTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
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
    public void GiftValueFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
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
    public void GiftValueChargeCaptureFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
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
    public void GiftValueChargeFundTest () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
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

    public void CheckoutWalkThroughSample () throws IOException, CurrencyMismatchException, BadParameterException, GiftCodeNotActiveException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();

        //this is your order
        int orderTotal = 7505;
        String orderCurrency = "USD";

        //set up your API keys
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        //get the stripe token and the gift code
        String stripeToken = properties.getProperty("stripe.demoToken");
        String giftCode = properties.getProperty("happyPath.code");

        //check how much the gift code can contribute to the checkout
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put("code", giftCode);
        giftValueParams.put("currency", orderCurrency);
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        int giftCodeShare = Math.min (orderTotal , giftCodeValue);
        int creditCardShare = orderTotal - giftCodeShare;

        if (creditCardShare == 0) { // the gift code can pay for the full order
            System.out.println(String.format("charging gift code for the entire order total, %s%s.", orderCurrency, giftCodeShare));
            Map<String, Object> giftChargeParams = new HashMap<>();
            giftChargeParams.put("code", giftCode);
            giftChargeParams.put("amount", giftCodeShare);
            giftChargeParams.put("currency", orderCurrency);
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        } else if (giftCodeShare > 0){ //the gift code can pay some and the remainder goes on the credit card
            //pending charge on gift code
            System.out.println(String.format("charging gift code %s%s.", orderCurrency, giftCodeValue));
            Map<String, Object> giftChargeParams = new HashMap<>();
            giftChargeParams.put("code", giftCode);
            giftChargeParams.put("amount", giftCodeShare);
            giftChargeParams.put("currency", orderCurrency);
            giftChargeParams.put("capture", false);
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

            // Charge the remainder on the credit card:
            System.out.println(String.format("charging credit card %s%s.", orderCurrency, creditCardShare));

            Map<String, Object> stripeParam = new HashMap<String, Object>();
            stripeParam.put("amount", creditCardShare);
            stripeParam.put("currency", orderCurrency);
            stripeParam.put("source", stripeToken);
            try {
                Charge charge = Charge.create(stripeParam);
                //capture gift code charge once the credit card transaction went through
                giftCharge.capture();
            } catch (IOException e) {
                giftCharge.cancel();
                throw new IOException(e);
            }
        }
        else { //entire order charged on credit card
            System.out.println(String.format("charging credit card for the entire order total, %s%s.", orderCurrency, creditCardShare));

            Map<String, Object> stripeParam = new HashMap<String, Object>();
            stripeParam.put("amount", creditCardShare);
            stripeParam.put("currency", orderCurrency);
            stripeParam.put("source", stripeToken);
            Charge charge = Charge.create(stripeParam);
        }
    }
}

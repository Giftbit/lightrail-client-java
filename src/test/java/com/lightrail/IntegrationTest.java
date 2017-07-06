package com.lightrail;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.model.business.GiftCharge;
import com.lightrail.model.business.GiftValue;
import com.lightrail.net.APICoreTest;
import com.lightrail.model.Lightrail;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IntegrationTest {

    @Test
    public void CheckoutWalkThroughTest () throws IOException, CurrencyMismatchException, BadParameterException, GiftCodeNotActiveException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Properties properties = APICoreTest.getProperties();

        //this is your order
        int orderTotal = 7537;
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
            giftChargeParams.put("amount", giftCodeValue);
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
            } catch (Exception e) {
                e.printStackTrace();
                giftCharge.cancel();
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

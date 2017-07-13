package com.lightrail.model.ecommerce;


import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.business.GiftFund;
import com.lightrail.model.business.GiftValue;
import com.stripe.Stripe;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class StripeGiftHybridChargeTest {

    public static GiftFund returnFundsToCode(int amount) throws IOException, AuthorizationException {
        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put(Constants.StripeParameters.AMOUNT, amount);

        return GiftFund.create(giftFundParams);
    }

    @Test
    public void hybridChargeHappyPathTest() throws IOException, ThirdPartyPaymentException, AuthorizationException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        int transactionAmount = 400;

        Map<String, Object> hybridChargeParams = TestParams.readCodeParamsFromProperties();
        hybridChargeParams.put(Constants.StripeParameters.AMOUNT, transactionAmount);
        hybridChargeParams.put(Constants.StripeParameters.TOKEN, properties.getProperty("stripe.demoToken"));

        PaymentSummary paymentSummary = StripeGiftHybridCharge.simulate(hybridChargeParams);
        int giftCodeShare = paymentSummary.getGiftCodeAmount();
        StripeGiftHybridCharge stripeGiftHybridCharge = StripeGiftHybridCharge.create(hybridChargeParams);

         paymentSummary = stripeGiftHybridCharge.getPaymentSummary();
         assertEquals(giftCodeShare, paymentSummary.getGiftCodeAmount());

        int creditCardShare = paymentSummary.getCreditCardAmount();

        assertEquals(transactionAmount, giftCodeShare + creditCardShare);

        returnFundsToCode(giftCodeShare);
    }

    @Test
    public void hybridChargeGiftCodeOnlyTest() throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException, InsufficientValueException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        int transactionAmount = giftValue.getCurrentValue() - 1;
        Map<String, Object> hybridChargeParams = TestParams.readCodeParamsFromProperties();
        hybridChargeParams.put(Constants.StripeParameters.AMOUNT, transactionAmount);

        PaymentSummary paymentSummary = StripeGiftHybridCharge.simulate(hybridChargeParams);
        assertEquals(0, paymentSummary.getCreditCardAmount());
        StripeGiftHybridCharge stripeGiftHybridCharge = StripeGiftHybridCharge.create(hybridChargeParams);
        paymentSummary = stripeGiftHybridCharge.getPaymentSummary();
        int giftCodeShare = paymentSummary.getGiftCodeAmount();
        assertEquals(0, paymentSummary.getCreditCardAmount());
        returnFundsToCode(giftCodeShare);
    }

    @Test
    public void hybridChargeCreditCardOnlyTest() throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException, InsufficientValueException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        int transactionAmount = 500;
        Map<String, Object> hybridChargeParams = new HashMap<>();
        hybridChargeParams.put(Constants.StripeParameters.CURRENCY, properties.getProperty("happyPath.code.currency"));
        hybridChargeParams.put(Constants.StripeParameters.AMOUNT, transactionAmount);
        hybridChargeParams.put(Constants.StripeParameters.TOKEN, properties.getProperty("stripe.demoToken"));

        PaymentSummary paymentSummary = StripeGiftHybridCharge.simulate(hybridChargeParams);
        assertEquals(0, paymentSummary.getGiftCodeAmount());
        StripeGiftHybridCharge stripeGiftHybridCharge = StripeGiftHybridCharge.create(hybridChargeParams);
        paymentSummary = stripeGiftHybridCharge.getPaymentSummary();
        assertEquals(0, paymentSummary.getGiftCodeAmount());
    }

    @Test
    public void hybridChargeGiftCodeOnlyButNotEnough () throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        int amountToGoOnCreditCard = 400;
        int transactionAmount = giftValue.getCurrentValue() + amountToGoOnCreditCard;
        try {
            Map<String, Object> hybridChargeParams = TestParams.readCodeParamsFromProperties();
            hybridChargeParams.put(Constants.StripeParameters.AMOUNT, transactionAmount);
            PaymentSummary paymentSummary = StripeGiftHybridCharge.simulate(hybridChargeParams);
            assertEquals( amountToGoOnCreditCard, paymentSummary.getCreditCardAmount());

            StripeGiftHybridCharge stripeGiftHybridCharge = StripeGiftHybridCharge.create(hybridChargeParams);
        } catch (Exception e) {
            assertEquals(e.getCause().getClass().getName(), BadParameterException.class.getName());
        }
    }
}

package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.Currency;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.business.GiftFund;
import com.stripe.Stripe;
import com.stripe.exception.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CheckoutWithGiftCodeTest {

    @Test
    public void checkoutWithGiftCodeTest() throws IOException, GiftCodeNotActiveException, CurrencyMismatchException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        CheckoutWithGiftCode checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(7645, "USD")
                .useGiftCode(properties.getProperty("happyPath.code"))
                .useStripeToken(properties.getProperty("stripe.demoToken"));

        PaymentSummary paymentSummary = checkoutWithGiftCode.checkout();
        int giftCodeShare = paymentSummary.getGiftCodeAmount();

        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put("amount", giftCodeShare);

        GiftFund giftCharge = GiftFund.create(giftFundParams);

        checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(7645, "USD")
                .useGiftCode(properties.getProperty("happyPath.code"))
                .useStripeToken(properties.getProperty("stripe.demoToken"));

        paymentSummary = checkoutWithGiftCode.getPaymentSummary();
        int newGiftCodeShare = paymentSummary.getGiftCodeAmount();
        assertEquals(giftCodeShare, newGiftCodeShare);
    }

    public void checkoutWithGiftCodeSample() throws IOException, GiftCodeNotActiveException, CurrencyMismatchException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, InsufficientValueException, AuthorizationException {
        Properties properties = TestParams.getProperties();

        //set up your api keys
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        CheckoutWithGiftCode checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(7645, "USD")
                .useGiftCode(properties.getProperty("happyPath.code"))
                .useStripeToken(properties.getProperty("stripe.demoToken"));
        PaymentSummary paymentSummary = checkoutWithGiftCode.getPaymentSummary();

        //show this summary to the user and get them to confirm
        System.out.println(paymentSummary);

        paymentSummary = checkoutWithGiftCode.checkout();
        //show final summary to the user
        System.out.println(paymentSummary);
    }
}

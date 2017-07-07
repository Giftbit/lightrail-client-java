package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.model.Lightrail;
import com.lightrail.net.APICoreTest;
import com.stripe.Stripe;
import com.stripe.exception.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class CheckoutWithGiftCodeTest {

    @Test
    public void checkoutWithGiftCodeHappyPathTest() throws IOException, GiftCodeNotActiveException, BadParameterException, CurrencyMismatchException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Properties properties = APICoreTest.getProperties();

        //set up your api keys
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        CheckoutWithGiftCode checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(76.45, "USD")
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

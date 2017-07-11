package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Currency;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.business.GiftFund;
import com.lightrail.model.business.GiftValue;
import com.stripe.Stripe;
import com.stripe.exception.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CheckoutWithGiftCodeTest {

    private CheckoutWithGiftCode checkoutAndRefundTheGiftCode (int orderTotal, boolean dontAddStripe) throws IOException, AuthorizationException, GiftCodeNotActiveException, CurrencyMismatchException, InsufficientValueException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        String orderCurrency = properties.getProperty("happyPath.code.currency");

        CheckoutWithGiftCode checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(orderTotal, orderCurrency)
                .useGiftCode(properties.getProperty("happyPath.code"));

        if (!dontAddStripe && checkoutWithGiftCode.needsCreditCardPayment()) {
            checkoutWithGiftCode.useStripeToken(properties.getProperty("stripe.demoToken"));
        }

        PaymentSummary paymentSummary = checkoutWithGiftCode.checkout();
        int giftCodeShare = paymentSummary.getGiftCodeAmount();

        Map<String, Object> giftFundParams = TestParams.readCardParamsFromProperties();
        giftFundParams.put("amount", giftCodeShare);

        GiftFund giftCharge = GiftFund.create(giftFundParams);
        return checkoutWithGiftCode;
    }

    @Test
    public void checkoutWithGiftCodeHappyPathWalkThroughTest() throws IOException, GiftCodeNotActiveException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();

        CheckoutWithGiftCode checkoutWithGiftCode = checkoutAndRefundTheGiftCode(7645, false);
        int giftCodeShare = checkoutWithGiftCode.getPaymentSummary().getGiftCodeAmount();

        checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(7645, "USD")
                .useGiftCode(properties.getProperty("happyPath.code"))
                .useStripeToken(properties.getProperty("stripe.demoToken"));

        PaymentSummary paymentSummary = checkoutWithGiftCode.getPaymentSummary();
        int newGiftCodeShare = paymentSummary.getGiftCodeAmount();
        assertEquals(giftCodeShare, newGiftCodeShare);
    }

    @Test
    public void checkoutWithGiftCodeOnlyTest () throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException, InsufficientValueException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();
        String orderCurrency = properties.getProperty("happyPath.code.currency");
        String giftCode = properties.getProperty("happyPath.code");

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        int orderTotal = giftValue.getCurrentValue() - 1;
        CheckoutWithGiftCode checkoutWithGiftCode = checkoutAndRefundTheGiftCode(orderTotal, false);
        PaymentSummary paymentSummary = checkoutWithGiftCode.getPaymentSummary();
        assertEquals(0, paymentSummary.getCreditCardAmount());
    }

    @Test
    public void checkoutWithoutCreditCardInfoWhenNeededTest () throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException, InsufficientValueException, ThirdPartyPaymentException {
        Properties properties = TestParams.getProperties();
        String orderCurrency = properties.getProperty("happyPath.code.currency");
        String giftCode = properties.getProperty("happyPath.code");

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        int orderTotal = giftValue.getCurrentValue() + 1;
        try {
            CheckoutWithGiftCode checkoutWithGiftCode = checkoutAndRefundTheGiftCode(orderTotal, true);
        } catch (Exception e) {
            assertEquals(e.getCause().getClass().getName(), BadParameterException.class.getName());
        }
    }

    @Test
    public void checkoutWithGiftCodeNoNeedForCreditCardTest () throws IOException, CurrencyMismatchException, AuthorizationException, GiftCodeNotActiveException {
        Properties properties = TestParams.getProperties();
        String orderCurrency = properties.getProperty("happyPath.code.currency");
        String giftCode = properties.getProperty("happyPath.code");

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Stripe.apiKey = properties.getProperty("stripe.testApiKey");

        Map<String, Object> giftValueParams = TestParams.readCodeParamsFromProperties();
        GiftValue giftValue = GiftValue.retrieve(giftValueParams);

        int orderTotal = giftValue.getCurrentValue() - 1;

        CheckoutWithGiftCode checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(orderTotal, orderCurrency)
                .useGiftCode(giftCode);

        assert (! checkoutWithGiftCode.needsCreditCardPayment());

        int newOrderTotal = giftValue.getCurrentValue() + 1;
        checkoutWithGiftCode = new CheckoutWithGiftCode()
                .setOrderTotal(newOrderTotal, orderCurrency)
                .useGiftCode(giftCode);

        assert (checkoutWithGiftCode.needsCreditCardPayment());

    }

    public void checkoutWithGiftCodeSample() throws IOException, GiftCodeNotActiveException, CurrencyMismatchException, InsufficientValueException, AuthorizationException, ThirdPartyPaymentException {
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

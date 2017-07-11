package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.model.business.GiftCharge;
import com.lightrail.model.business.GiftValue;
import com.stripe.exception.*;
import com.stripe.model.Charge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CheckoutWithGiftCode {
    int orderTotal;
    String orderCurrency;
    String giftCode;
    String stripeToken;

    public CheckoutWithGiftCode() {
    }

    public CheckoutWithGiftCode useGiftCode(String giftCode) {
        this.giftCode = giftCode;
        return this;
    }

    public CheckoutWithGiftCode useStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
        return this;
    }


    public CheckoutWithGiftCode setOrderTotal(int orderTotal, String orderCurrency) {
        this.orderTotal = orderTotal;
        this.orderCurrency = orderCurrency;
        return this;
    }

    private int determineGiftCodeShare() throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(Constants.LightrailParameters.CODE, giftCode);
        giftValueParams.put(Constants.LightrailParameters.CURRENCY, orderCurrency);
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        return Math.min(orderTotal, giftCodeValue);
    }

    public boolean needsCreditCardPayment() throws GiftCodeNotActiveException, IOException, CurrencyMismatchException, InsufficientValueException, AuthorizationException {
        int giftCodeShare = determineGiftCodeShare();
        return (giftCodeShare == orderTotal);
    }

    public PaymentSummary getPaymentSummary() throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
        int giftCodeShare = determineGiftCodeShare();
        int creditCardShare = orderTotal - giftCodeShare;

        PaymentSummary paymentSummary = new PaymentSummary(orderCurrency);
        paymentSummary.addGiftCodeAmount(giftCodeShare);
        paymentSummary.addCreditCardAmount(creditCardShare);

        return paymentSummary;
    }

    private Map<String, Object> getStripeParams(int amount, String currency) {
        Map<String, Object> stripeParam = new HashMap<>();
        stripeParam.put("amount", amount);
        stripeParam.put("currency", currency);
        stripeParam.put("source", stripeToken);
        return stripeParam;
    }

    public PaymentSummary checkout() throws BadParameterException, CurrencyMismatchException, GiftCodeNotActiveException, IOException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, InsufficientValueException, AuthorizationException {
        int giftCodeShare = determineGiftCodeShare();
        int creditCardShare = orderTotal - giftCodeShare;

        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(Constants.LightrailParameters.CODE, giftCode);
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, giftCodeShare);
        giftChargeParams.put(Constants.LightrailParameters.CURRENCY, orderCurrency);

        if (creditCardShare == 0) { // the gift code can pay for the full order
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        } else if (giftCodeShare > 0) { //the gift code can pay some and the remainder goes on the credit card
            giftChargeParams.put(Constants.LightrailParameters.CAPTURE, false);
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
            try {
                Charge charge = Charge.create(getStripeParams(creditCardShare, orderCurrency));
                giftCharge.capture();
            } catch (Exception e) {
                e.printStackTrace();
                giftCharge.cancel();
            }
        } else { //entire order charged on credit card
            Charge charge = Charge.create(getStripeParams(creditCardShare, orderCurrency));
        }

        return new PaymentSummary(orderCurrency, giftCodeShare, creditCardShare);
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public float getOrderTotal() {
        return orderTotal;
    }
}

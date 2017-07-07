package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.helpers.Currency;
import com.lightrail.helpers.LightrailParameters;
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

    public CheckoutWithGiftCode setOrderTotal(Double orderTotal, String orderCurrency) {
        setOrderTotal(orderTotal.floatValue(), orderCurrency);
        return this;
    }

    public CheckoutWithGiftCode setOrderTotal(Float orderTotal, String orderCurrency) {
        this.orderTotal = Currency.majorToMinor(orderTotal, orderCurrency);
        this.orderCurrency = orderCurrency;
        return this;
    }

    private int determineGiftCodeShare() throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailParameters.CODE, giftCode);
        giftValueParams.put(LightrailParameters.CURRENCY, orderCurrency);
        int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();

        return Math.min(orderTotal, giftCodeValue);
    }

    public PaymentSummary getPaymentSummary() throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException {
        int giftCodeShare = determineGiftCodeShare();
        int creditCardShare = orderTotal - giftCodeShare;

        PaymentSummary paymentSummary = new PaymentSummary(orderCurrency);
        paymentSummary.addGiftCodeAmount(giftCodeShare);
        paymentSummary.addCreditCardAmount(creditCardShare);

        return paymentSummary;
    }

    public PaymentSummary checkout() throws BadParameterException, CurrencyMismatchException, GiftCodeNotActiveException, IOException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        int giftCodeShare = determineGiftCodeShare();
        int creditCardShare = orderTotal - giftCodeShare;

        if (creditCardShare == 0) { // the gift code can pay for the full order
            Map<String, Object> giftChargeParams = new HashMap<>();
            giftChargeParams.put(LightrailParameters.CODE, giftCode);
            giftChargeParams.put(LightrailParameters.AMOUNT, giftCodeShare);
            giftChargeParams.put(LightrailParameters.CURRENCY, orderCurrency);
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        } else if (giftCodeShare > 0) { //the gift code can pay some and the remainder goes on the credit card
            //pending charge on gift code
            Map<String, Object> giftChargeParams = new HashMap<>();
            giftChargeParams.put(LightrailParameters.CODE, giftCode);
            giftChargeParams.put(LightrailParameters.AMOUNT, giftCodeShare);
            giftChargeParams.put(LightrailParameters.CURRENCY, orderCurrency);
            giftChargeParams.put(LightrailParameters.CAPTURE, false);
            GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

            // Charge the remainder on the credit card:
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
        } else { //entire order charged on credit card
            Map<String, Object> stripeParam = new HashMap<String, Object>();
            stripeParam.put("amount", creditCardShare);
            stripeParam.put("currency", orderCurrency);
            stripeParam.put("source", stripeToken);
            Charge charge = Charge.create(stripeParam);
        }

        return new PaymentSummary(orderCurrency, giftCodeShare, creditCardShare);

    }

}

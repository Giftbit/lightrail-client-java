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
    private int orderTotal;
    private String orderCurrency;
    private String giftCode = null;
    private String stripeToken = null;
    private String stripeCustomer = null;

    StripeGiftHybridCharge stripeGiftHybridChargeObject = null;

    public CheckoutWithGiftCode(int orderTotal, String orderCurrency) {
        setOrderTotal(orderTotal, orderCurrency);
    }

    public CheckoutWithGiftCode useGiftCode(String giftCode) {
        this.giftCode = giftCode;
        return this;
    }

    public CheckoutWithGiftCode useStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
        return this;
    }

    public CheckoutWithGiftCode useStripeCustomer (String stripeCustomer) {
        this.stripeCustomer = stripeCustomer;
        return this;
    }

    public CheckoutWithGiftCode setOrderTotal(int orderTotal, String orderCurrency) {
        this.orderTotal = orderTotal;
        this.orderCurrency = orderCurrency;
        return this;
    }

    public boolean needsCreditCardPayment() throws GiftCodeNotActiveException, IOException, CurrencyMismatchException, AuthorizationException, InsufficientValueException {
        PaymentSummary paymentSummary = StripeGiftHybridCharge.simulate(getChargeParams());
        return paymentSummary.getCreditCardAmount() >0;
    }

    public PaymentSummary getPaymentSummary() throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, AuthorizationException {
        if (stripeGiftHybridChargeObject != null) {
            return stripeGiftHybridChargeObject.getPaymentSummary();
        } else {
            return StripeGiftHybridCharge.simulate(getChargeParams());
        }
    }

    private Map<String, Object> getChargeParams() {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put(Constants.LightrailParameters.AMOUNT, orderTotal);
        chargeParams.put(Constants.LightrailParameters.CURRENCY, orderCurrency);
        if (giftCode != null) {
            chargeParams.put(Constants.LightrailParameters.CODE, giftCode);
        }
        if (stripeToken != null) {
            chargeParams.put(Constants.StripeParameters.TOKEN, stripeToken);
        }
        if (stripeCustomer != null) {
            chargeParams.put(Constants.StripeParameters.CUSTOMER, stripeCustomer);
        }

        return chargeParams;
    }

    public PaymentSummary checkout() throws ThirdPartyPaymentException, InsufficientValueException, GiftCodeNotActiveException, CurrencyMismatchException, AuthorizationException, IOException {
        stripeGiftHybridChargeObject = StripeGiftHybridCharge.create(getChargeParams());
        return stripeGiftHybridChargeObject.getPaymentSummary();
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public int getOrderTotal() {
        return orderTotal;
    }
}

package com.lightrail.model.ecommerce;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.model.business.GiftCharge;
import com.lightrail.model.business.GiftValue;
import com.stripe.model.Charge;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StripeGiftHybridCharge {

    GiftCharge giftCharge = null;
    Charge stripeCharge = null;

    PaymentSummary paymentSummary = null;


    public GiftCharge getGiftCharge() {
        return giftCharge;
    }

    public Charge getStripeCharge() {
        return stripeCharge;
    }

    public PaymentSummary getPaymentSummary() {
        return paymentSummary;
    }

    public StripeGiftHybridCharge(GiftCharge giftCharge, Charge stripeCharge, PaymentSummary paymentSummary) {
        this.giftCharge = giftCharge;
        this.stripeCharge = stripeCharge;
        this.paymentSummary = paymentSummary;
    }

    private static Map<String, Object> getStripeParams(int amount, Map<String, Object> chargeParams) {
        Object stripeToken = chargeParams.get(Constants.StripeParameters.TOKEN);
        Object stripeCustomerId = chargeParams.get(Constants.StripeParameters.CUSTOMER);

        Map<String, Object> stripeParam = new HashMap<>();

        if (stripeToken != null) {
            stripeParam.put(Constants.StripeParameters.TOKEN, stripeToken);
        } else if (stripeCustomerId != null) {
            stripeParam.put(Constants.StripeParameters.CUSTOMER, stripeCustomerId);
        } else {
            throw new BadParameterException("Need credit card payment information to handle this order.");
        }
        stripeParam.put(Constants.StripeParameters.AMOUNT, amount);
        stripeParam.put(Constants.StripeParameters.CURRENCY, chargeParams.get(Constants.StripeParameters.CURRENCY));
        return stripeParam;
    }

    private static int determineGiftCodeShare(Map<String, Object> chargeParams) throws BadParameterException, IOException, CurrencyMismatchException, GiftCodeNotActiveException, AuthorizationException, InsufficientValueException {
        int transactionAmount = (Integer) chargeParams.get(Constants.LightrailParameters.AMOUNT);

        int giftCodeShare = 0;

        Object giftCodeObject = chargeParams.get(Constants.LightrailParameters.CODE);
        if (giftCodeObject != null) {
            String giftCode = (String) giftCodeObject;
            Map<String, Object> giftValueParams = new HashMap<>();
            giftValueParams.put(Constants.LightrailParameters.CODE, giftCode);
            giftValueParams.put(Constants.LightrailParameters.CURRENCY, chargeParams.get(Constants.LightrailParameters.CURRENCY));
            int giftCodeValue = GiftValue.retrieve(giftValueParams).getCurrentValue();
            if (giftCodeValue == 0)
                throw new InsufficientValueException("The gift code does not have any available value.");
            giftCodeShare = Math.min(transactionAmount, giftCodeValue);
        }
        return giftCodeShare;
    }

    public static PaymentSummary simulate(Map<String, Object> chargeParams) throws AuthorizationException, CurrencyMismatchException, GiftCodeNotActiveException, InsufficientValueException, IOException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), chargeParams);
        int transactionAmount = (Integer) chargeParams.get(Constants.LightrailParameters.AMOUNT);

        int giftCodeShare = determineGiftCodeShare(chargeParams);
        int creditCardShare = transactionAmount - giftCodeShare;
        return new PaymentSummary((String) chargeParams.get(Constants.LightrailParameters.CURRENCY),
                giftCodeShare,
                creditCardShare);
    }

    public static StripeGiftHybridCharge create(Map<String, Object> chargeParams) throws InsufficientValueException, AuthorizationException, CurrencyMismatchException, GiftCodeNotActiveException, IOException, ThirdPartyPaymentException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), chargeParams);

        GiftCharge giftCharge = null;
        Charge stripeCharge = null;

        int transactionAmount = (Integer) chargeParams.get(Constants.LightrailParameters.AMOUNT);
        String transactionCurrency = (String) chargeParams.get(Constants.LightrailParameters.CURRENCY);

        int giftCodeShare = determineGiftCodeShare(chargeParams);
        int creditCardShare = transactionAmount - giftCodeShare;

        if (giftCodeShare != 0) {
            Map<String, Object> giftChargeParams = new HashMap<>();
            giftChargeParams.put(Constants.LightrailParameters.CODE, chargeParams.get(Constants.LightrailParameters.CODE));
            giftChargeParams.put(Constants.LightrailParameters.AMOUNT, giftCodeShare);
            giftChargeParams.put(Constants.LightrailParameters.CURRENCY, transactionCurrency);

            if (creditCardShare == 0) { //everything on giftcode
                giftCharge = GiftCharge.create(giftChargeParams);
            } else { //split between giftcode and credit card
                giftChargeParams.put(Constants.LightrailParameters.CAPTURE, false);
                giftCharge = GiftCharge.create(giftChargeParams);
                try {
                    stripeCharge = Charge.create(getStripeParams(creditCardShare, chargeParams));
                } catch (Exception e) {
                    giftCharge.cancel();
                    throw new ThirdPartyPaymentException(e);
                }
                giftCharge.capture();
            }
        } else { //all on credit card
            try {
                stripeCharge = Charge.create(getStripeParams(creditCardShare, chargeParams));
            } catch (Exception e) {
                throw new ThirdPartyPaymentException(e);
            }
        }

        PaymentSummary paymentSummary = new PaymentSummary(transactionCurrency, giftCodeShare, creditCardShare);
        return new StripeGiftHybridCharge(giftCharge, stripeCharge, paymentSummary);
    }
    public String getGiftTransactionId() {
        String giftCodeTransactionId =  null;
        if (giftCharge != null)
            giftCodeTransactionId = giftCharge.getTransactionId();
        return giftCodeTransactionId;
    }

    public String getStripeTransactionId () {
        String stripeTransactionId = null;
        if (stripeCharge != null)
            stripeTransactionId = stripeCharge.getId();
        return stripeTransactionId;
    }
}

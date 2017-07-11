package com.lightrail.model.ecommerce;

import com.lightrail.helpers.*;

import java.util.*;
import java.util.Currency;

public class PaymentSummary {

    class PaymentSummaryLine {
        String title;
        int amount;
    }

    Map<String, PaymentSummaryLine> summaryLineItems = new HashMap<>();
    String currency;

    public PaymentSummary(String currency, int giftCodeAmount, int creditCardAmount) {
        this.currency = currency;
        addGiftCodeAmount(giftCodeAmount);
        addCreditCardAmount(creditCardAmount);

    }

    public PaymentSummary(String currency) {
        this.currency = currency;
    }

    public void addGiftCodeAmount(int giftCodeAmount) {
        PaymentSummaryLine paymentSummaryLine = new PaymentSummaryLine();

        paymentSummaryLine.title = String.format(Constants.LightrailEcommerce.PaymentSummary.GIFT_CODE_SHARE);
        paymentSummaryLine.amount = giftCodeAmount;
        summaryLineItems.put(Constants.LightrailEcommerce.PaymentSummary.GIFT_CODE_SHARE, paymentSummaryLine);
    }

    public void addCreditCardAmount(int creditCardAmount) {
        PaymentSummaryLine paymentSummaryLine = new PaymentSummaryLine();

        paymentSummaryLine.title = String.format(Constants.LightrailEcommerce.PaymentSummary.CREDIT_CARD_SHARE);
        paymentSummaryLine.amount = creditCardAmount;
        summaryLineItems.put(Constants.LightrailEcommerce.PaymentSummary.CREDIT_CARD_SHARE, paymentSummaryLine);
    }

    public int getGiftCodeAmount() {
        return summaryLineItems.get(Constants.LightrailEcommerce.PaymentSummary.GIFT_CODE_SHARE).amount;
    }

    public int getCreditCardAmount() {
        return summaryLineItems.get(Constants.LightrailEcommerce.PaymentSummary.CREDIT_CARD_SHARE).amount;
    }


    public String toString() {
        StringBuffer orderSummaryOutputBuffer = new StringBuffer();
        for (String summaryItemKey : summaryLineItems.keySet()) {
            orderSummaryOutputBuffer.append(summaryLineItems.get(summaryItemKey).title).append("\t:")
                    .append(Currency.getInstance(currency).getSymbol())
                    .append(String.valueOf(summaryLineItems.get(summaryItemKey).amount)).append("\n");
        }
        return orderSummaryOutputBuffer.toString();
    }
}

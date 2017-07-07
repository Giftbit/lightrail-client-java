package com.lightrail.model.ecommerce;

import com.lightrail.helpers.*;

import java.util.*;
import java.util.Currency;

public class PaymentSummary {

    class PaymentSummaryLine {
        String title;
        float amount;
    }

    List<PaymentSummaryLine> summaryLineItems = new ArrayList<>();
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
        paymentSummaryLine.amount = com.lightrail.helpers.Currency.minorToMajor(giftCodeAmount, currency);
        summaryLineItems.add(paymentSummaryLine);
    }

    public void addCreditCardAmount(int creditCardAmount) {
        PaymentSummaryLine paymentSummaryLine = new PaymentSummaryLine();
        paymentSummaryLine.title = String.format(Constants.LightrailEcommerce.PaymentSummary.CREDIT_CARD_SHARE);
        paymentSummaryLine.amount = com.lightrail.helpers.Currency.minorToMajor(creditCardAmount, currency);
        summaryLineItems.add(paymentSummaryLine);
    }

    public String toString() {
        StringBuffer orderSummaryOutputBuffer = new StringBuffer();
        for (PaymentSummaryLine summaryItem : summaryLineItems) {
            orderSummaryOutputBuffer.append(summaryItem.title).append("\t:")
                    .append(Currency.getInstance(currency).getSymbol())
                    .append(String.valueOf(summaryItem.amount)).append("\n");
        }
        return orderSummaryOutputBuffer.toString();
    }
}

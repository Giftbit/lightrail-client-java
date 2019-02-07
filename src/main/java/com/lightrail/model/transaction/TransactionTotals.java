package com.lightrail.model.transaction;

import java.util.Objects;

public class TransactionTotals {

    public Integer subtotal;
    public Integer tax;
    public Integer discount;
    public Integer discountLightrail;
    public Integer paidLightrail;
    public Integer paidStripe;
    public Integer paidInternal;
    public Integer payable;
    public Integer remainder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTotals that = (TransactionTotals) o;
        return Objects.equals(subtotal, that.subtotal) &&
                Objects.equals(tax, that.tax) &&
                Objects.equals(discount, that.discount) &&
                Objects.equals(discountLightrail, that.discountLightrail) &&
                Objects.equals(paidLightrail, that.paidLightrail) &&
                Objects.equals(paidStripe, that.paidStripe) &&
                Objects.equals(paidInternal, that.paidInternal) &&
                Objects.equals(payable, that.payable) &&
                Objects.equals(remainder, that.remainder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtotal, tax, discount, discountLightrail, paidLightrail, paidStripe, paidInternal, payable, remainder);
    }
}

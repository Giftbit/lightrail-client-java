package com.lightrail.model.transaction;

import java.util.Objects;

public class LineTotal {

    public Integer subtotal;
    public Integer taxable;
    public Integer tax;
    public Integer discount;
    public Integer remainder;
    public Integer payable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineTotal lineTotal = (LineTotal) o;
        return Objects.equals(subtotal, lineTotal.subtotal) &&
                Objects.equals(taxable, lineTotal.taxable) &&
                Objects.equals(tax, lineTotal.tax) &&
                Objects.equals(discount, lineTotal.discount) &&
                Objects.equals(remainder, lineTotal.remainder) &&
                Objects.equals(payable, lineTotal.payable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtotal, taxable, tax, discount, remainder, payable);
    }
}

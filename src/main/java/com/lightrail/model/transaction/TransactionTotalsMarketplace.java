package com.lightrail.model.transaction;

import java.util.Objects;

public class TransactionTotalsMarketplace {

    public Integer sellerDiscount;
    public Integer sellerGross;
    public Integer sellerNet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTotalsMarketplace that = (TransactionTotalsMarketplace) o;
        return Objects.equals(sellerDiscount, that.sellerDiscount) &&
                Objects.equals(sellerGross, that.sellerGross) &&
                Objects.equals(sellerNet, that.sellerNet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerDiscount, sellerGross, sellerNet);
    }
}

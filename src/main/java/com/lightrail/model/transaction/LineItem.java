package com.lightrail.model.transaction;

import java.util.Map;
import java.util.Objects;

public class LineItem {

    public String type;
    public String productId;
    public String variantId;
    public Integer unitPrice;
    public Integer quantity;
    public Float taxRate;
    public Float marketplaceRate;
    public Map<String, Object> metadata;
    public LineTotal lineTotal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineItem lineItem = (LineItem) o;
        return Objects.equals(type, lineItem.type) &&
                Objects.equals(productId, lineItem.productId) &&
                Objects.equals(variantId, lineItem.variantId) &&
                Objects.equals(unitPrice, lineItem.unitPrice) &&
                Objects.equals(quantity, lineItem.quantity) &&
                Objects.equals(taxRate, lineItem.taxRate) &&
                Objects.equals(marketplaceRate, lineItem.marketplaceRate) &&
                Objects.equals(metadata, lineItem.metadata) &&
                Objects.equals(lineTotal, lineItem.lineTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, productId, variantId, unitPrice, quantity, taxRate, marketplaceRate, metadata, lineTotal);
    }
}

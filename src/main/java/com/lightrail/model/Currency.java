package com.lightrail.model;

import java.util.Objects;

public class Currency {

    public String code;
    public String name;
    public String symbol;
    public int decimalPlaces;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return decimalPlaces == currency.decimalPlaces &&
                Objects.equals(code, currency.code) &&
                Objects.equals(name, currency.name) &&
                Objects.equals(symbol, currency.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, symbol, decimalPlaces);
    }
}

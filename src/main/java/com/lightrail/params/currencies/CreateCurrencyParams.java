package com.lightrail.params.currencies;

public class CreateCurrencyParams {
    public String code;
    public String name;
    public String symbol;
    public int decimalPlaces;

    CreateCurrencyParams() {
    }

    CreateCurrencyParams(String code) {
        this.code = code;
    }
}

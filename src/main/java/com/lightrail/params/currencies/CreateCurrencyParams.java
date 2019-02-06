package com.lightrail.params.currencies;

public class CreateCurrencyParams {
    public String code;
    public String name;
    public String symbol;
    public int decimalPlaces;

    public CreateCurrencyParams() {
    }

    public CreateCurrencyParams(String code) {
        this.code = code;
    }
}

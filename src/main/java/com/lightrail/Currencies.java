package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.Currency;
import com.lightrail.model.PaginatedList;
import com.lightrail.params.currencies.CreateCurrencyParams;
import com.lightrail.params.currencies.UpdateCurrencyParams;

import java.io.IOException;

import static com.lightrail.network.NetworkUtils.urlEncode;

public class Currencies {
    private final LightrailClient lr;

    public Currencies(LightrailClient lr) {
        this.lr = lr;
    }

    public Currency createCurrency(CreateCurrencyParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/currencies", params, Currency.class);
    }

    public Currency getCurrency(String code) throws IOException, LightrailRestException {
        NullArgumentException.check(code, "code");

        return lr.networkProvider.get(String.format("/currencies/%s", urlEncode(code)), Currency.class);
    }

    public PaginatedList<Currency> listCurrencies() throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList("/currencies", Currency.class);
    }

    public Currency updateCurrency(String code, UpdateCurrencyParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(code, "code");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.patch(String.format("/currencies/%s", urlEncode(code)), params, Currency.class);
    }

    public Currency updateCurrency(Currency currency, UpdateCurrencyParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(currency, "currency");
        NullArgumentException.check(params, "params");

        return updateCurrency(currency.code, params);
    }
}

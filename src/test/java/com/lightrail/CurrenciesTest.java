package com.lightrail;

import com.lightrail.model.Currency;
import com.lightrail.model.PaginatedList;
import com.lightrail.params.currencies.CreateCurrencyParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.*;

public class CurrenciesTest {

    private LightrailClient lc;

    @Before
    public void setUp() {
        lc = getLightrailClient();
    }

    @After
    public void tearDown() {
        lc = null;
    }

    @Test
    public void listCurrencies() throws Exception {
        PaginatedList<Currency> currencies = lc.currencies.listCurrencies();
        assertNotNull(currencies);
    }

    @Test
    public void createAndDeleteCurrency() throws Exception {
        CreateCurrencyParams params = new CreateCurrencyParams();
        params.code = "TEST_C";
        params.name = "Test Currency";
        params.symbol = "T";
        params.decimalPlaces = 0;

        Currency currency = lc.currencies.createCurrency(params);
        assertEquals(params.code, currency.code);
        assertEquals(params.name, currency.name);
        assertEquals(params.symbol, currency.symbol);
        assertEquals(params.decimalPlaces, currency.decimalPlaces);

        // If this code fails for any reason you'll need to delete the currency
        // manually to get the tests working again.
        lc.currencies.deleteCurrency(currency);
    }
}

package com.lightrail;

import com.lightrail.model.Currency;
import com.lightrail.model.PaginatedList;
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
}

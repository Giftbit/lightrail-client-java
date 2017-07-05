package com.lightrail.model;

import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.net.APICoreTest;
import com.lightrail.net.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftValueTest {

    @Test
    public void GifValueReadHappyPath () throws IOException, CurrencyMismatchException, GiftCodeNotActiveException {
        Properties properties = APICoreTest.getProperties();

        Lightrail.apiKey = properties.getProperty("testApiKey");

        Map<String, Object> giftValueParams = new HashMap<String, Object>();
        giftValueParams.put("code", properties.getProperty("happyPath.code"));
        giftValueParams.put("currency", properties.getProperty("happyPath.code.currency"));

        GiftValue giftValue = GiftValue.retrieve(giftValueParams);
        DecimalFormat formatter = new DecimalFormat("###.##");
        formatter.setDecimalSeparatorAlwaysShown(true);
        formatter.setMinimumFractionDigits(2);
        assertEquals(properties.getProperty("happyPath.code.value"), formatter.format(giftValue.getValue()));
        assertEquals(properties.getProperty("happyPath.code.currency"), giftValue.getCurrency());
    }
}

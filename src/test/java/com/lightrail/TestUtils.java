package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.Currency;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.params.currencies.CreateCurrencyParams;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestUtils {

    private static Dotenv dotenv;

    public static LightrailClient getLightrailClient() {
        Dotenv dotenv = getDotenv();
        LightrailClient c = new LightrailClient(dotenv.get("LIGHTRAIL_API_KEY"));
        ((DefaultNetworkProvider) c.getNetworkProvider()).setRestRoot(dotenv.get("LIGHTRAIL_API_PATH"));
        return c;
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    private static Dotenv getDotenv() {
        if (dotenv == null) {
            dotenv = Dotenv.configure()
                    .directory("src/test/resources")
                    .load();
        }
        return dotenv;
    }

    static Currency getOrCreateTestCurrency(LightrailClient lc) throws IOException, LightrailRestException {
        Currency currency;
        try {
            currency = lc.currencies.getCurrency("CAF");
        } catch (LightrailRestException ignored) {
            CreateCurrencyParams currencyParams = new CreateCurrencyParams();
            currencyParams.code = "CAF";
            currencyParams.name = "CoffeeBucks";
            currencyParams.decimalPlaces = 2;
            currencyParams.symbol = "$";
            currency = lc.currencies.createCurrency(currencyParams);
            assertEquals(currency.code, currencyParams.code);
        }

        return currency;
    }
}

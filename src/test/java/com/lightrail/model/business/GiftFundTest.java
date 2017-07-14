package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftFundTest {

    @Test
    public void GiftFundHappyPathTest () throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        int fundAmount = 500;

        Map<String, Object> giftChargeParams = TestParams.readCardParamsFromProperties();
        giftChargeParams.put("amount", fundAmount);

        GiftFund giftCharge = GiftFund.create(giftChargeParams);
        assertEquals(giftCharge.getAmount(), fundAmount);
    }
}

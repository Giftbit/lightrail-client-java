package com.lightrail.model.business;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.net.APICoreTest;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GiftChargeTest {

    @Test
    public void GiftChargeCapturedCreateHappyPath () throws IOException, BadParameterException {
        Properties properties = APICoreTest.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftChargeParams = new HashMap<String, Object>();
        giftChargeParams.put("code", properties.getProperty("happyPath.code"));
        giftChargeParams.put("currency", properties.getProperty("happyPath.code.currency"));
        giftChargeParams.put("amount", 1.01);
        giftChargeParams.put("userSuppliedId", "hah0000000001");

        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
        assertEquals(((Double) giftChargeParams.get("amount")).floatValue(), giftCharge.getAmount(),0f);
        assertEquals(giftChargeParams.get("userSuppliedId"), giftCharge.getUserSuppliedId());
        System.out.println();
    }

    @Test
    public void GiftChargeAuthCancel() throws IOException, BadParameterException {
        Properties properties = APICoreTest.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftChargeParams = new HashMap<String, Object>();
        giftChargeParams.put("code", properties.getProperty("happyPath.code"));
        giftChargeParams.put("currency", properties.getProperty("happyPath.code.currency"));
        giftChargeParams.put("amount", 1.01);
        giftChargeParams.put("capture", false);


        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

        giftCharge.cancel();

        System.out.println();
    }

    @Test
    public void GiftChargeAuthCapture() throws IOException, BadParameterException {
        Properties properties = APICoreTest.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Map<String, Object> giftChargeParams = new HashMap<String, Object>();
        giftChargeParams.put("code", properties.getProperty("happyPath.code"));
        giftChargeParams.put("currency", properties.getProperty("happyPath.code.currency"));
        giftChargeParams.put("amount", 1.01);
        giftChargeParams.put("capture", false);


        GiftCharge giftCharge = GiftCharge.create(giftChargeParams);

        giftCharge.capture();

        System.out.println();
    }

}

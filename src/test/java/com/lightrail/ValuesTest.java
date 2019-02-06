package com.lightrail;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.RedemptionRule;
import com.lightrail.model.Value;
import com.lightrail.params.values.CreateValueParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static com.lightrail.TestUtils.generateId;
import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.*;

public class ValuesTest {

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
    public void createAndGetValue() throws IOException, LightrailRestException {
        CreateValueParams params = new CreateValueParams(generateId());
        params.currency = "USD";
        params.balance = 500;
        params.redemptionRule = new RedemptionRule("1 == 1", "always true");
        params.usesRemaining = 1;
        params.startDate = new Date(33450364800000L);
        params.endDate = new Date(65322892800000L);
        params.metadata = new JsonObject();
        params.metadata.add("hip", new JsonPrimitive("to be square"));

        Value createdValue = lc.values.createValue(params);
        assertEquals(params.id, createdValue.id);
        assertEquals(params.currency, createdValue.currency);
        assertEquals(params.balance, createdValue.balance);
        assertNull(createdValue.balanceRule);
        assertNotNull(createdValue.redemptionRule);
        assertEquals(params.redemptionRule.rule, createdValue.redemptionRule.rule);
        assertEquals(params.redemptionRule.explanation, createdValue.redemptionRule.explanation);
        assertEquals(params.usesRemaining, createdValue.usesRemaining);
        assertEquals(params.startDate, createdValue.startDate);
        assertEquals(params.endDate, createdValue.endDate);
        assertNotNull(createdValue.metadata);
        assertEquals(params.metadata.get("hip"), createdValue.metadata.get("hip"));
        assertNotNull(createdValue.createdDate);
        assertNotNull(createdValue.updatedDate);
        assertNotNull(createdValue.createdBy);

        Value gettedValue = lc.values.getValue(params.id);
        assertEquals(createdValue.id, gettedValue.id);
        assertEquals(createdValue.currency, gettedValue.currency);
        assertEquals(createdValue.balance, gettedValue.balance);
        assertNull(gettedValue.balanceRule);
        assertNotNull(gettedValue.redemptionRule);
        assertEquals(createdValue.redemptionRule.rule, gettedValue.redemptionRule.rule);
        assertEquals(createdValue.redemptionRule.explanation, gettedValue.redemptionRule.explanation);
        assertEquals(createdValue.usesRemaining, gettedValue.usesRemaining);
        assertEquals(createdValue.startDate, gettedValue.startDate);
        assertEquals(createdValue.endDate, gettedValue.endDate);
        assertNotNull(gettedValue.metadata);
        assertEquals(createdValue.metadata.get("hip"), gettedValue.metadata.get("hip"));
        assertEquals(createdValue.createdDate, gettedValue.createdDate);
        assertEquals(createdValue.updatedDate, gettedValue.updatedDate);
        assertEquals(createdValue.createdBy, gettedValue.createdBy);
    }
}

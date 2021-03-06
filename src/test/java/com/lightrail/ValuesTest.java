package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.*;
import com.lightrail.params.contacts.AttachContactToValueParams;
import com.lightrail.params.contacts.CreateContactParams;
import com.lightrail.params.values.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

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
        params.metadata = new HashMap<>();
        params.metadata.put("hip", "to be square");

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
        assertEquals(params.metadata, createdValue.metadata);
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

        ListValuesParams listParams = new ListValuesParams();
        listParams.id = createdValue.id;

        PaginatedList<Value> valueList = lc.values.listValues(listParams);
        assertEquals(1, valueList.size());
        assertEquals(createdValue.id, valueList.get(0).id);
    }

    @Test
    public void createAndAttachGenericValue() throws IOException, LightrailRestException {
        CreateContactParams contactParams = new CreateContactParams(generateId());
        Contact contactCreated = lc.contacts.createContact(contactParams);
        assertEquals(contactParams.id, contactCreated.id);

        GenericCodeOptions genericCodeOptions = new GenericCodeOptions(new PerContactGenericCodeOptions(null, 5));
        CreateValueParams genericValueParams = new CreateValueParams(generateId());
        genericValueParams.currency = "USD";
        genericValueParams.balanceRule = new BalanceRule("500", "$5 the hard way");
        genericValueParams.isGenericCode = true;
        genericValueParams.genericCodeOptions = genericCodeOptions;

        Value genericValue = lc.values.createValue(genericValueParams);
        assertEquals(genericValueParams.id, genericValue.id);
        assertEquals(genericValueParams.currency, genericValue.currency);
        assertNull(genericValueParams.balance);
        assertNotNull(genericValue.genericCodeOptions);
        assertNotNull(genericValueParams.genericCodeOptions.perContact);
        assertNull(genericValue.genericCodeOptions.perContact.balance);
        assertEquals(genericValueParams.genericCodeOptions.perContact.usesRemaining, genericValue.genericCodeOptions.perContact.usesRemaining);
        assertNotNull(genericValue.balanceRule);
        assertEquals(genericValueParams.balanceRule.rule, genericValue.balanceRule.rule);
        assertEquals(genericValueParams.balanceRule.explanation, genericValue.balanceRule.explanation);
        assertNull(genericValue.usesRemaining);
        assertNotNull(genericValue.createdDate);
        assertNotNull(genericValue.updatedDate);
        assertNotNull(genericValue.createdBy);

        AttachContactToValueParams attachParams = new AttachContactToValueParams();
        attachParams.valueId = genericValue.id;
        Value attachedValue = lc.contacts.attachContactToValue(contactCreated.id, attachParams);
        assertEquals(genericValue.id, attachedValue.attachedFromValueId);
        assertNotNull(attachedValue.balanceRule);
        assertEquals(genericValue.balanceRule.rule, attachedValue.balanceRule.rule);
        assertEquals(genericValue.balanceRule.explanation, attachedValue.balanceRule.explanation);
        assertNull(attachedValue.balance);
        assertEquals(genericValue.genericCodeOptions.perContact.usesRemaining, attachedValue.usesRemaining);
        assertNotNull(genericValue.createdDate);
        assertNotNull(genericValue.updatedDate);
        assertNotNull(genericValue.createdBy);
    }

    @Test
    public void updateValues() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.code = generateId();
        createValueParams.currency = "USD";
        createValueParams.balanceRule = new BalanceRule("500", "$5");
        createValueParams.redemptionRule = new RedemptionRule("1 == 1", "always true");
        createValueParams.usesRemaining = 1;

        Value createdValue = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, createdValue.id);

        UpdateValueParams updateValueParams = new UpdateValueParams();
        updateValueParams.canceled = Optional.of(true);

        Value updatedValue = lc.values.updateValue(createdValue, updateValueParams);
        assertEquals(createdValue.id, updatedValue.id);
        assertTrue(updatedValue.canceled);

        ChangeValuesCodeParams changeCodeParams = new ChangeValuesCodeParams();
        changeCodeParams.code = generateId();
        Value codeChangedValue = lc.values.changeValuesCode(createdValue, changeCodeParams);
        assertEquals(createdValue.id, codeChangedValue.id);

        GetValueQueryParams getValueQueryParams = new GetValueQueryParams();
        getValueQueryParams.showCode = true;
        Value getCodeChangedValue = lc.values.getValue(createdValue.id, getValueQueryParams);
        assertEquals(createdValue.id, getCodeChangedValue.id);
        assertNotEquals(createdValue.code, getCodeChangedValue.code);
    }

    @Test
    public void paginateValues() throws Exception {
        ListValuesParams params = new ListValuesParams();
        params.limit = 1;

        PaginatedList<Value> valuesStart = lc.values.listValues(params);
        assertEquals(1, valuesStart.size());
        assertFalse(valuesStart.hasFirst());
        assertFalse(valuesStart.hasPrevious());
        assertTrue(valuesStart.hasNext());
        assertTrue(valuesStart.hasLast());

        PaginatedList<Value> valuesNext = valuesStart.getNext();
        assertEquals(1, valuesNext.size());
        assertTrue(valuesNext.hasFirst());
        assertTrue(valuesNext.hasPrevious());
        assertTrue(valuesNext.hasNext());
        assertTrue(valuesNext.hasLast());

        PaginatedList<Value> valuesPrev = valuesNext.getPrevious();
        assertEquals(1, valuesPrev.size());
        assertEquals(valuesStart.get(0).id, valuesPrev.get(0).id);
        assertTrue(valuesPrev.hasNext());
        assertTrue(valuesPrev.hasLast());

        PaginatedList<Value> valuesFirst = valuesNext.getFirst();
        assertEquals(1, valuesFirst.size());
        assertEquals(valuesStart.get(0).id, valuesFirst.get(0).id);
        assertFalse(valuesFirst.hasFirst());
        assertFalse(valuesFirst.hasPrevious());
        assertTrue(valuesFirst.hasNext());
        assertTrue(valuesFirst.hasLast());

        PaginatedList<Value> valuesLast = valuesNext.getLast();
        assertEquals(1, valuesLast.size());
        assertTrue(valuesLast.hasFirst());
        assertTrue(valuesLast.hasPrevious());
        assertFalse(valuesLast.hasNext());
        assertFalse(valuesLast.hasLast());
    }
}

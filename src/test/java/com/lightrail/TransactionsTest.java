package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.Value;
import com.lightrail.model.transaction.Transaction;
import com.lightrail.model.transaction.step.LightrailTransactionStep;
import com.lightrail.model.transaction.step.TransactionStep;
import com.lightrail.params.transactions.DebitParams;
import com.lightrail.params.transactions.DebitSource;
import com.lightrail.params.values.CreateValueParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.lightrail.TestUtils.generateId;
import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.*;

public class TransactionsTest {

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
    public void debit() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.currency = "USD";
        createValueParams.balance = 500;

        Value value = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, value.id);

        DebitParams debitParams = new DebitParams(generateId());
        debitParams.source = new DebitSource();
        debitParams.source.valueId = value.id;
        debitParams.currency = "USD";
        debitParams.amount = 350;

        Transaction debit = lc.transactions.debit(debitParams);
        assertEquals(debitParams.id, debit.id);
        assertEquals("debit", debit.transactionType);
        assertEquals(debitParams.currency, debit.currency);
        assertEquals(1, debit.steps.size());
        assertTrue(debit.steps.get(0) instanceof LightrailTransactionStep);

        LightrailTransactionStep debitStep = (LightrailTransactionStep) debit.steps.get(0);
        assertEquals(value.id, debitStep.valueId);
        assertEquals(new Integer(500), debitStep.balanceBefore);
        assertEquals(new Integer(150), debitStep.balanceAfter);
        assertEquals(new Integer(-350), debitStep.balanceChange);

        Transaction tx = lc.transactions.getTransaction(debitParams.id);
        assertEquals(debit, tx);
    }
}

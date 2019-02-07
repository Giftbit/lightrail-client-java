package com.lightrail;

import com.lightrail.model.PaginatedList;
import com.lightrail.model.Value;
import com.lightrail.model.transaction.Transaction;
import com.lightrail.model.transaction.step.LightrailTransactionStep;
import com.lightrail.model.transaction.step.StripeTransactionStep;
import com.lightrail.params.transactions.*;
import com.lightrail.params.values.CreateValueParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        debitParams.source = new LightrailTransactionSource();
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

    @Test
    public void credit() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.currency = "USD";
        createValueParams.balance = 0;

        Value value = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, value.id);

        CreditParams creditParams = new CreditParams(generateId());
        creditParams.destination = new LightrailTransactionDestination();
        creditParams.destination.valueId = value.id;
        creditParams.currency = "USD";
        creditParams.amount = 350;

        Transaction credit = lc.transactions.credit(creditParams);
        assertEquals(creditParams.id, credit.id);
        assertEquals("credit", credit.transactionType);
        assertEquals(creditParams.currency, credit.currency);
        assertEquals(1, credit.steps.size());
        assertTrue(credit.steps.get(0) instanceof LightrailTransactionStep);

        LightrailTransactionStep creditStep = (LightrailTransactionStep) credit.steps.get(0);
        assertEquals(value.id, creditStep.valueId);
        assertEquals(new Integer(0), creditStep.balanceBefore);
        assertEquals(new Integer(350), creditStep.balanceAfter);
        assertEquals(new Integer(350), creditStep.balanceChange);

        Transaction tx = lc.transactions.getTransaction(creditParams.id);
        assertEquals(credit, tx);
    }

    @Test
    public void transferBetweenLightrailValues() throws Exception {
        CreateValueParams createValueSourceParams = new CreateValueParams(generateId());
        createValueSourceParams.currency = "USD";
        createValueSourceParams.balance = 100;
        Value valueSource = lc.values.createValue(createValueSourceParams);
        assertEquals(createValueSourceParams.id, valueSource.id);

        CreateValueParams createValueDestParams = new CreateValueParams(generateId());
        createValueDestParams.currency = "USD";
        createValueDestParams.balance = 100;
        Value valueDest = lc.values.createValue(createValueDestParams);
        assertEquals(createValueDestParams.id, valueDest.id);

        LightrailTransactionSource txSource = new LightrailTransactionSource();
        txSource.valueId = valueSource.id;

        LightrailTransactionDestination txDest = new LightrailTransactionDestination();
        txDest.valueId = valueDest.id;

        TransferParams txParams = new TransferParams(generateId());
        txParams.source = txSource;
        txParams.destination = txDest;
        txParams.currency = "USD";
        txParams.amount = 70;

        Transaction tx = lc.transactions.transfer(txParams);
        assertEquals(txParams.id, tx.id);
        assertEquals("transfer", tx.transactionType);
        assertEquals(txParams.currency, tx.currency);
        assertEquals(2, tx.steps.size());
        assertTrue(tx.steps.get(0) instanceof LightrailTransactionStep);
        assertTrue(tx.steps.get(1) instanceof LightrailTransactionStep);

        Transaction txGet = lc.transactions.getTransaction(tx.id);
        assertEquals(tx, txGet);
    }

    @Test
    public void transferFromStripeToLightrail() throws Exception {
        CreateValueParams createValueDestParams = new CreateValueParams(generateId());
        createValueDestParams.currency = "USD";
        createValueDestParams.balance = 0;
        Value valueDest = lc.values.createValue(createValueDestParams);
        assertEquals(createValueDestParams.id, valueDest.id);

        StripeTransactionSource txSource = new StripeTransactionSource();
        txSource.source = "tok_visa";

        LightrailTransactionDestination txDest = new LightrailTransactionDestination();
        txDest.valueId = valueDest.id;

        TransferParams txParams = new TransferParams(generateId());
        txParams.source = txSource;
        txParams.destination = txDest;
        txParams.currency = "USD";
        txParams.amount = 5000;
        txParams.simulate = true;

        Transaction tx = lc.transactions.transfer(txParams);
        assertEquals(txParams.id, tx.id);
        assertEquals("transfer", tx.transactionType);
        assertEquals(txParams.currency, tx.currency);
        assertEquals(2, tx.steps.size());

        LightrailTransactionStep destStep;
        StripeTransactionStep stripeStep;

        if (tx.steps.get(0) instanceof LightrailTransactionStep) {
            destStep = (LightrailTransactionStep) tx.steps.get(0);
            stripeStep = (StripeTransactionStep) tx.steps.get(1);
        } else {
            destStep = (LightrailTransactionStep) tx.steps.get(1);
            stripeStep = (StripeTransactionStep) tx.steps.get(0);
        }

        assertNotNull(destStep);
        assertNotNull(stripeStep);
    }

    @Test
    public void reverse() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.currency = "USD";
        createValueParams.balance = 5600;

        Value value = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, value.id);

        DebitParams debitParams = new DebitParams(generateId());
        debitParams.source = new LightrailTransactionSource();
        debitParams.source.valueId = value.id;
        debitParams.currency = "USD";
        debitParams.amount = 3300;

        Transaction debit = lc.transactions.debit(debitParams);
        assertEquals(debitParams.id, debit.id);
        assertEquals("debit", debit.transactionType);
        assertEquals(debitParams.currency, debit.currency);
        assertEquals(1, debit.steps.size());
        assertTrue(debit.steps.get(0) instanceof LightrailTransactionStep);

        ReverseParams reverseParams = new ReverseParams(generateId());

        Transaction reverse = lc.transactions.reverse(debit, reverseParams);
        assertEquals(reverseParams.id, reverse.id);
        assertEquals("reverse", reverse.transactionType);
        assertEquals(debitParams.currency, reverse.currency);
        assertEquals(1, reverse.steps.size());
        assertTrue(reverse.steps.get(0) instanceof LightrailTransactionStep);

        PaginatedList<Transaction> txChain = lc.transactions.getTransactionChain(debit);
        assertEquals(2, txChain.size());
    }

    @Test
    public void capturePending() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.currency = "USD";
        createValueParams.balance = 9400;

        Value value = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, value.id);

        DebitParams debitParams = new DebitParams(generateId());
        debitParams.source = new LightrailTransactionSource();
        debitParams.source.valueId = value.id;
        debitParams.currency = "USD";
        debitParams.amount = 6100;
        debitParams.pending = true;

        Transaction debit = lc.transactions.debit(debitParams);
        assertEquals(debitParams.id, debit.id);
        assertEquals("debit", debit.transactionType);
        assertEquals(debitParams.currency, debit.currency);
        assertEquals(1, debit.steps.size());
        assertTrue(debit.steps.get(0) instanceof LightrailTransactionStep);

        CapturePendingParams capturePendingParams = new CapturePendingParams(generateId());

        Transaction capture = lc.transactions.capturePending(debit, capturePendingParams);
        assertEquals(capturePendingParams.id, capture.id);
        assertEquals("capture", capture.transactionType);
        assertEquals(debitParams.currency, capture.currency);
        assertEquals(0, capture.steps.size());

        PaginatedList<Transaction> txChain = lc.transactions.getTransactionChain(capture);
        assertEquals(2, txChain.size());
    }

    @Test
    public void voidPending() throws Exception {
        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.currency = "USD";
        createValueParams.balance = 9400;

        Value value = lc.values.createValue(createValueParams);
        assertEquals(createValueParams.id, value.id);

        DebitParams debitParams = new DebitParams(generateId());
        debitParams.source = new LightrailTransactionSource();
        debitParams.source.valueId = value.id;
        debitParams.currency = "USD";
        debitParams.amount = 6100;
        debitParams.pending = true;

        Transaction debit = lc.transactions.debit(debitParams);
        assertEquals(debitParams.id, debit.id);
        assertEquals("debit", debit.transactionType);
        assertEquals(debitParams.currency, debit.currency);
        assertEquals(1, debit.steps.size());
        assertTrue(debit.steps.get(0) instanceof LightrailTransactionStep);

        VoidPendingParams voidPendingParams = new VoidPendingParams(generateId());

        Transaction voidTx = lc.transactions.voidPending(debit, voidPendingParams);
        assertEquals(voidPendingParams.id, voidTx.id);
        assertEquals("void", voidTx.transactionType);
        assertEquals(debitParams.currency, voidTx.currency);
        assertEquals(1, voidTx.steps.size());
        assertTrue(voidTx.steps.get(0) instanceof LightrailTransactionStep);

        PaginatedList<Transaction> txChain = lc.transactions.getTransactionChain(voidTx);
        assertEquals(2, txChain.size());
    }

    @Test
    public void paginateTransactions() throws Exception {
        ListTransactionsParams params = new ListTransactionsParams();
        params.limit = 1;

        PaginatedList<Transaction> transactionsStart = lc.transactions.listTransactions(params);
        assertEquals(1, transactionsStart.size());
        assertFalse(transactionsStart.hasFirst());
        assertFalse(transactionsStart.hasPrevious());
        assertTrue(transactionsStart.hasNext());
        assertTrue(transactionsStart.hasLast());

        PaginatedList<Transaction> transactionsNext = transactionsStart.getNext();
        assertEquals(1, transactionsNext.size());
        assertTrue(transactionsNext.hasFirst());
        assertTrue(transactionsNext.hasPrevious());
        assertTrue(transactionsNext.hasNext());
        assertTrue(transactionsNext.hasLast());

        PaginatedList<Transaction> transactionsPrev = transactionsNext.getPrevious();
        assertEquals(1, transactionsPrev.size());
        assertEquals(transactionsStart.get(0).id, transactionsPrev.get(0).id);
        assertTrue(transactionsPrev.hasNext());
        assertTrue(transactionsPrev.hasLast());

        PaginatedList<Transaction> transactionsFirst = transactionsNext.getFirst();
        assertEquals(1, transactionsFirst.size());
        assertEquals(transactionsStart.get(0).id, transactionsFirst.get(0).id);
        assertFalse(transactionsFirst.hasFirst());
        assertFalse(transactionsFirst.hasPrevious());
        assertTrue(transactionsFirst.hasNext());
        assertTrue(transactionsFirst.hasLast());

        PaginatedList<Transaction> transactionsLast = transactionsNext.getLast();
        assertEquals(1, transactionsLast.size());
        assertTrue(transactionsLast.hasFirst());
        assertTrue(transactionsLast.hasPrevious());
        assertFalse(transactionsLast.hasNext());
        assertFalse(transactionsLast.hasLast());
    }
}

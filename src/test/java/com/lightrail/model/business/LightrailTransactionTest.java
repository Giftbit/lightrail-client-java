package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class LightrailTransactionTest {

    @Test
    public void walkThrough() throws IOException, CouldNotFindObjectException, AuthorizationException, InsufficientValueException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        String code = properties.getProperty("happyPath.code");
        String cardId = properties.getProperty("happyPath.code.cardId");
        String currency = properties.getProperty("happyPath.code.currency");

        Integer transactionValue = 150;

        String baseUserSuppliedId = UUID.randomUUID().toString();

        //create by giving parameters: card
        Map<String, Object> transactionParams = TestParams.readCardParamsFromProperties();
        transactionParams.put(LightrailConstants.Parameters.VALUE, transactionValue);
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-card");
        LightrailTransaction transaction = LightrailTransaction.create(transactionParams);


        assertEquals(baseUserSuppliedId + "-by-card", transaction.getUserSuppliedId());
        String transactionId = transaction.getTransactionId();

        LightrailTransaction retrievedTransaction = LightrailTransaction.retrieveByCardIdAndTransactionId(cardId, transactionId);
        assertEquals(baseUserSuppliedId + "-by-card", retrievedTransaction.getUserSuppliedId());

        retrievedTransaction = LightrailTransaction.retrieveByCardIdAndUserSuppliedId(cardId, baseUserSuppliedId + "-by-card");
        assertEquals(transactionId, retrievedTransaction.getTransactionId());

        //create by giving parameters: code
        transactionParams = TestParams.readCodeParamsFromProperties();
        transactionParams.put(LightrailConstants.Parameters.VALUE, 0 - transactionValue);
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-code");

        LightrailTransaction simulatedTransaction1 = LightrailTransaction.simulate(transactionParams);
        LightrailTransaction simulatedTransaction2 = LightrailTransaction.simulateByCode(code, 0 - transactionValue, currency);
        LightrailTransaction simulatedTransaction3 = LightrailTransaction.simulateByCardId(cardId, 0 - transactionValue, currency);
        transaction = LightrailTransaction.create(transactionParams);

        assertEquals(simulatedTransaction1.getValue(), transaction.getValue());
        assertEquals(simulatedTransaction2.getValue(), transaction.getValue());
        assertEquals(simulatedTransaction3.getValue(), transaction.getValue());

        assertEquals(baseUserSuppliedId + "-by-code", transaction.getUserSuppliedId());

        transactionId = transaction.getTransactionId();

        retrievedTransaction = LightrailTransaction.retrieveByCodeAndTransactionId(code, transactionId);
        assertEquals(baseUserSuppliedId + "-by-code", retrievedTransaction.getUserSuppliedId());

        retrievedTransaction = LightrailTransaction.retrieveByCodeAndUserSuppliedId(code, baseUserSuppliedId + "-by-code");
        assertEquals(transactionId, retrievedTransaction.getTransactionId());

        //create pending/capture by cardId
        LightrailTransaction pendingTransaction = LightrailTransaction.createPendingByCardId(cardId, 0-transactionValue, currency);
        LightrailTransaction captureTransaction = pendingTransaction.capture();
        assertEquals(pendingTransaction.getTransactionId(), captureTransaction.getParentTransactionId());

        LightrailTransaction refundTransaction = captureTransaction.refund();
        assertEquals(captureTransaction.getTransactionId(), refundTransaction.getParentTransactionId());

        //create pending/capture by code
        pendingTransaction = LightrailTransaction.createPendingByCode(code, 0-transactionValue, currency);
        LightrailTransaction voidTransaction = pendingTransaction.doVoid();
        assertEquals(pendingTransaction.getTransactionId(), voidTransaction.getParentTransactionId());
    }

}

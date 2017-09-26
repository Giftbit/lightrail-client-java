package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.objects.Metadata;
import com.lightrail.model.api.objects.RequestParameters;
import org.junit.Test;

import java.io.IOException;
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
        RequestParameters transactionParams = TestParams.readCardParamsFromProperties();
        transactionParams.put(LightrailConstants.Parameters.VALUE, transactionValue);
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-card");
        LightrailTransaction transaction = LightrailTransaction.Create.create(transactionParams);

        try {
            transaction.capture();
        } catch (Exception e) {
            assert e instanceof BadParameterException;
        }
        try {
            transaction.doVoid();
        } catch (Exception e) {
            assert e instanceof BadParameterException;
        }


        assertEquals(baseUserSuppliedId + "-by-card", transaction.getUserSuppliedId());
        String transactionId = transaction.getTransactionId();

        LightrailTransaction retrievedTransaction = LightrailTransaction.Retrieve.byCardIdAndTransactionId(cardId, transactionId);
        assertEquals(baseUserSuppliedId + "-by-card", retrievedTransaction.getUserSuppliedId());

        retrievedTransaction = LightrailTransaction.Retrieve.byCardIdAndUserSuppliedId(cardId, baseUserSuppliedId + "-by-card");
        assertEquals(transactionId, retrievedTransaction.getTransactionId());

        RequestParameters retrieveParams = new RequestParameters();
        retrieveParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        retrieveParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-card");
        retrievedTransaction = LightrailTransaction.Retrieve.retrieve(retrieveParams);
        assertEquals(transactionId, retrievedTransaction.getTransactionId());

        retrieveParams = new RequestParameters();
        retrieveParams.put(LightrailConstants.Parameters.CODE, code);
        retrieveParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-card");
        retrievedTransaction = LightrailTransaction.Retrieve.retrieve(retrieveParams);
        assertEquals(transactionId, retrievedTransaction.getTransactionId());


        //create by giving parameters: code
        transactionParams = TestParams.readCodeParamsFromProperties();
        transactionParams.put(LightrailConstants.Parameters.VALUE, 0 - transactionValue);
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, baseUserSuppliedId + "-by-code");

        LightrailTransaction simulatedTransaction1 = LightrailTransaction.Simulate.simulate(transactionParams);
        LightrailTransaction simulatedTransaction2 = LightrailTransaction.Simulate.byCode(code, 0 - transactionValue, currency);
        LightrailTransaction simulatedTransaction3 = LightrailTransaction.Simulate.byCardId(cardId, 0 - transactionValue, currency);
        transaction = LightrailTransaction.Create.create(transactionParams);

        assertEquals(simulatedTransaction1.getValue(), transaction.getValue());
        assertEquals(simulatedTransaction2.getValue(), transaction.getValue());
        assertEquals(simulatedTransaction3.getValue(), transaction.getValue());

        assertEquals(baseUserSuppliedId + "-by-code", transaction.getUserSuppliedId());

        transactionId = transaction.getTransactionId();

        retrievedTransaction = LightrailTransaction.Retrieve.byCodeAndTransactionId(code, transactionId);
        assertEquals(baseUserSuppliedId + "-by-code", retrievedTransaction.getUserSuppliedId());

        retrievedTransaction = LightrailTransaction.Retrieve.byCodeAndUserSuppliedId(code, baseUserSuppliedId + "-by-code");
        assertEquals(transactionId, retrievedTransaction.getTransactionId());

        //create pending/capture by cardId with cascaded metadata including arrays
        Metadata innerMetadata = new Metadata();
        innerMetadata.put("test", "test");
        String [] arrayTest = new String []{"test1", "test2", "test3"};
        innerMetadata.put("innerArray", arrayTest);
        Metadata metadata = new Metadata();
        metadata.put("innerObject", innerMetadata);
        metadata.put("array", arrayTest);
        LightrailTransaction pendingTransaction = LightrailTransaction.Create.pendingByCardId(cardId, 0-transactionValue, currency, metadata);
        Metadata returnedMetadata = pendingTransaction.getMetadata();

        for (Object key: metadata.keySet()) {
            assert (returnedMetadata.containsKey(key));
        }

        LightrailTransaction captureTransaction = pendingTransaction.capture(metadata);
        assertEquals(pendingTransaction.getTransactionId(), captureTransaction.getParentTransactionId());

        LightrailTransaction refundTransaction = captureTransaction.refund();
        assertEquals(captureTransaction.getTransactionId(), refundTransaction.getParentTransactionId());

        //create pending/capture by code
        try {
            LightrailTransaction.Create.pendingByCode(code, transactionValue, currency);
        } catch (Exception e)
        {
            assert e instanceof BadParameterException;
        }
        pendingTransaction = LightrailTransaction.Create.pendingByCode(code, 0-transactionValue, currency);
        LightrailTransaction voidTransaction = pendingTransaction.doVoid();
        assertEquals(pendingTransaction.getTransactionId(), voidTransaction.getParentTransactionId());
    }

}

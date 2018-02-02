//package com.lightrail.model.business;
//
//import com.lightrail.exceptions.AuthorizationException;
//import com.lightrail.exceptions.CouldNotFindObjectException;
//import com.lightrail.exceptions.InsufficientValueException;
//import com.lightrail.helpers.LightrailConstants;
//import com.lightrail.helpers.TestParams;
//import com.lightrail.model.Lightrail;
//import com.lightrail.model.api.objects.CardDetails;
//import com.lightrail.model.api.objects.Metadata;
//import com.lightrail.model.api.objects.RequestParameters;
//import com.lightrail.model.api.objects.Transaction;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.Properties;
//import java.util.UUID;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class LightrailGiftCardTest {
//
//    @Test
//    public void walkThroughHappyPathTest() throws IOException, CouldNotFindObjectException, AuthorizationException, InsufficientValueException {
//        Properties properties = TestParams.getProperties();
//        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
//
//        String programId = properties.getProperty("happyPath.code.programId");
//        String currency = properties.getProperty("happyPath.code.currency");
//
//        Metadata metadata = new Metadata();
//        metadata.put("testKey", "testValue");
//
//        GiftCard createdGiftCard = GiftCard.create(programId, 400, metadata);
//
//        assertEquals(400, createdGiftCard.retrieveMaximumValue());
//
//        String fullCode = createdGiftCard.retrieveFullCode();
//        String cardId = createdGiftCard.getCardId();
//        String userSuppliedId = createdGiftCard.getUserSuppliedId();
//
//        assertEquals(userSuppliedId, GiftCard.retrieveByCardId(cardId).getUserSuppliedId());
//        assertEquals(cardId, GiftCard.retrieveByUserSuppliedId(userSuppliedId).getCardId());
//
//        LightrailTransaction fund = LightrailTransaction.Create.byCardId(cardId, 200, currency);
//
//        assertEquals(600, createdGiftCard.retrieveMaximumValue());
//
//        LightrailTransaction charge = LightrailTransaction.Create.byCode(fullCode, -200, currency);
//
//        assertEquals(400, createdGiftCard.retrieveMaximumValue());
//
//
//        String fundTxId = fund.getTransactionId();
//        String fundUserSuppliedId = fund.getUserSuppliedId();
//        String chargeTxId = fund.getTransactionId();
//        String chargeUserSuppliedId = fund.getUserSuppliedId();
//
//        Transaction retrievedFund = LightrailTransaction.Retrieve.byCardIdAndUserSuppliedId(cardId, fundUserSuppliedId);
//        Transaction retrievedCharge = LightrailTransaction.Retrieve.byCodeAndUserSuppliedId(fullCode, chargeUserSuppliedId);
//
//        assertEquals(fundTxId, retrievedFund.getTransactionId());
//        assertEquals(chargeTxId, retrievedCharge.getTransactionId());
//
//
//        GiftCard retrievedGiftCard = GiftCard.retrieveByCardId(cardId);
//        assertEquals(createdGiftCard.getDateCreated(), retrievedGiftCard.getDateCreated());
//        assertEquals(createdGiftCard.getCurrency(), retrievedGiftCard.getCurrency());
//        assertEquals(fullCode, retrievedGiftCard.retrieveFullCode());
//
//        createdGiftCard.freeze();
//
//        CardDetails cardDetails = GiftCard.retrieveCardDetailsByCode(fullCode);
//        assertEquals(LightrailConstants.API.ValueStores.STATE_FROZEN, cardDetails.getState());
//
//        retrievedGiftCard.unfreeze();
//        cardDetails = createdGiftCard.retrieveCardDetails();
//        assertEquals(LightrailConstants.API.ValueStores.STATE_ACTIVE, cardDetails.getState());
//    }
//
//    @Test
//    public void createCardWithExpiryDate() throws IOException, CouldNotFindObjectException, AuthorizationException {
//        Properties properties = TestParams.getProperties();
//        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
//
//        String programId = properties.getProperty("happyPath.code.programId");
//        String startDate = "2017-08-02T00:27:02.910Z";
//        String expiryDate = "2019-10-02T00:27:02.910Z";
//
//        GiftCard createdGiftCard = GiftCard.create(programId, 400, startDate, expiryDate);
//        CardDetails cardDetails = GiftCard.retrieveCardDetailsByCode(createdGiftCard.retrieveFullCode());
//
//        assertEquals(startDate, cardDetails.getStartDate());
//        assertEquals(expiryDate, cardDetails.getExpires());
//    }
//
//    @Test
//    public void createCardWithUserSuppliedId() throws IOException, CouldNotFindObjectException, AuthorizationException {
//        Properties properties = TestParams.getProperties();
//        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
//
//        String programId = properties.getProperty("happyPath.code.programId");
//        int initialValue = 400;
//        String userSuppliedId = UUID.randomUUID().toString();
//
//        RequestParameters params = new RequestParameters();
//        params.put(LightrailConstants.Parameters.PROGRAM_ID, programId);
//        params.put(LightrailConstants.Parameters.INITIAL_VALUE, initialValue);
//        params.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);
//        GiftCard createdGiftCard = GiftCard.create(params);
//
//        assertEquals(userSuppliedId, createdGiftCard.getUserSuppliedId());
//    }
//}

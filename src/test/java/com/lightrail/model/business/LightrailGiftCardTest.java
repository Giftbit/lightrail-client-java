package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;

import java.io.IOException;
import java.util.Properties;

import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.Transaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class LightrailGiftCardTest {

    @Test
    public void walkThroughHappyPathTest() throws IOException, CouldNotFindObjectException, AuthorizationException, InsufficientValueException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        String programId = properties.getProperty("happyPath.code.programId");
        String currency = properties.getProperty("happyPath.code.currency");

        GiftCard createdGiftCard = GiftCard.create(programId, 400);

        String fullCode = createdGiftCard.retrieveFullCode();
        String cardId = createdGiftCard.getCardId();

        LightrailTransaction fund = LightrailTransaction.createByCardId(cardId, 200, currency);
        LightrailTransaction charge = LightrailTransaction.createByCode(fullCode, -200, currency);

        String fundTxId= fund.getTransactionId();
        String fundUserSuppliedId= fund.getUserSuppliedId();
        String chargeTxId= fund.getTransactionId();
        String chargeUserSuppliedId= fund.getUserSuppliedId();

//        Transaction retrievedFund = LightrailTransaction.retrieveByCardAndUserSuppliedId(cardId, fundUserSuppliedId); //known BE issue.
        Transaction retrievedCharge = LightrailTransaction.retrieveByCodeAndUserSuppliedId(fullCode, chargeUserSuppliedId);

//        assertEquals(fundTxId, retrievedFund.getTransactionId());
        assertEquals(chargeTxId, retrievedCharge.getTransactionId());


        GiftCard retrievedGiftCard = GiftCard.retrieve(cardId);
        assertEquals(createdGiftCard.getDateCreated(), retrievedGiftCard.getDateCreated());
        assertEquals(createdGiftCard.getCurrency(), retrievedGiftCard.getCurrency());
        assertEquals(fullCode, retrievedGiftCard.retrieveFullCode());

        createdGiftCard.freeze();

        assertEquals(LightrailConstants.API.Balance.FROZEN, retrievedGiftCard.getState());

        retrievedGiftCard.unfreeze();
        assertEquals(LightrailConstants.API.Balance.ACTIVE, createdGiftCard.getState());
        assertEquals(LightrailConstants.API.Balance.ACTIVE, createdGiftCard.retrieveState());
    }

    @Test
    public void createCardWithExpiryDate() throws IOException, CouldNotFindObjectException, AuthorizationException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        String programId = properties.getProperty("happyPath.code.programId");
        String startDate = "2017-08-02T00:27:02.910Z";
        String expiryDate = "2017-10-02T00:27:02.910Z";

        GiftCard createdGiftCard = GiftCard.create(programId, 400, startDate, expiryDate);

        assertEquals(startDate, createdGiftCard.getStartDate());
        assertEquals(startDate, createdGiftCard.retrieveStartDate());
        assertEquals(expiryDate, createdGiftCard.getExpires());
        assertEquals(expiryDate, createdGiftCard.retrieveExpires());
    }

    @Test
    public void retrieveCard () throws AuthorizationException, CouldNotFindObjectException, IOException {
        Properties properties = TestParams.getProperties();

        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        Card card = GiftCard.retrieve("card-6d13a30197ce4c88ba76a1621a402a89");
        System.out.println();
    }
}

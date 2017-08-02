package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;

import java.io.IOException;
import java.util.Properties;

import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GiftCardTest {

    @Test
    public void walkThroughHappyPathTest() throws IOException, CouldNotFindObjectException, AuthorizationException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        String programId = properties.getProperty("happyPath.code.programId");

        GiftCard createdGiftCard = GiftCard.create(programId, 400);

        String fullCode = createdGiftCard.retrieveFullCode();

        String cardId = createdGiftCard.getCardId();

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
}

package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.Transaction;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GiftCard extends Card {
    LightrailValue balance;


    public String retrieveFullCode() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.retrieveCardsFullCode(getCardId()).getCode();
    }

    private void retrieveBalance() throws AuthorizationException, CouldNotFindObjectException, IOException {
        balance = LightrailValue.retrieveByCardId(getCardId());
    }
    private LightrailValue getBalance() throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (balance == null) {
            retrieveBalance();
        }
        return balance;
    }

    public String getState () throws AuthorizationException, CouldNotFindObjectException, IOException {
        return getBalance().getState();

    }
    public String retrieveState () throws AuthorizationException, CouldNotFindObjectException, IOException {
        retrieveBalance();
        return getState();
    }

    public String getExpires () throws AuthorizationException, CouldNotFindObjectException, IOException {
        return getBalance().getExpires();
    }

    public String retrieveExpires() throws AuthorizationException, CouldNotFindObjectException, IOException {
        retrieveBalance();
        return getExpires();
    }

    public String getStartDate () throws AuthorizationException, CouldNotFindObjectException, IOException {
        return getBalance().getStartDate();
    }

    public String retrieveStartDate () throws AuthorizationException, CouldNotFindObjectException, IOException {
        retrieveBalance();
        return getStartDate();
    }

    public LightrailActionTransaction freeze() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return freeze(new HashMap<String, Object>());
    }

    public LightrailActionTransaction unfreeze () throws AuthorizationException, CouldNotFindObjectException, IOException {
        return unfreeze(new HashMap<String, Object>());
    }

    public LightrailActionTransaction freeze(Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);
        Transaction transaction = APICore.actionOnCard(getCardId(), LightrailConstants.API.Cards.FREEZE, transactionParams);
        return new LightrailActionTransaction(transaction);
    }

    public LightrailActionTransaction unfreeze (Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);
        Transaction transaction = APICore.actionOnCard(getCardId(), LightrailConstants.API.Cards.UNFREEZE, transactionParams);
        return new LightrailActionTransaction(transaction);
    }

    public static GiftCard create(String programId, int initialValue, String startDate, String expiryDate) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(LightrailConstants.Parameters.PROGRAM_ID, programId);
        params.put(LightrailConstants.Parameters.INITIAL_VALUE, initialValue);
        if (startDate != null)
            params.put(LightrailConstants.Parameters.START_DATE, startDate);
        if (expiryDate != null)
            params.put(LightrailConstants.Parameters.EXPIRES, expiryDate);
        return create(params);
    }

    public static GiftCard create(String programId, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return create(programId, initialValue, null,null);
    }

    public static GiftCard create(Map<String, Object> cardCreationParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return (GiftCard) LightrailCard.createGiftCard(cardCreationParams);
    }

    public static GiftCard retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return (GiftCard) LightrailCard.retrieve(cardId, GiftCard.class);
    }
}

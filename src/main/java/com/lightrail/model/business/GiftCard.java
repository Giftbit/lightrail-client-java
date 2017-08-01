package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.Balance;
import com.lightrail.model.api.Card;
import com.lightrail.model.api.Transaction;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiftCard {
    Card card;
    LightrailValue balance;

    private GiftCard(Card card) {
        this.card = card;
    }

    public String getCardId() {
        return card.getCardId();
    }

    public String getDateCreated() {
        return card.getDateCreated();
    }

    public String getCurrency() {
        return card.getCurrency();
    }

    public String retrieveFullCode() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.retrieveCardsFullCode(getCardId()).getCode();
    }

    private void retrieveBalance() throws AuthorizationException, CouldNotFindObjectException, IOException {
        balance = LightrailValue.retrieveByCardId(card.getCardId());
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
        transactionParams = LightrailTransaction.addDefaultIdempotencyKeyIfNotProvided(transactionParams);
        Transaction transaction = APICore.actionOnCard(getCardId(), LightrailConstants.API.Cards.FREEZE, transactionParams);
        return new LightrailActionTransaction(transaction);
    }

    public LightrailActionTransaction unfreeze (Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        transactionParams = LightrailTransaction.addDefaultIdempotencyKeyIfNotProvided(transactionParams);
        Transaction transaction = APICore.actionOnCard(getCardId(), LightrailConstants.API.Cards.UNFREEZE, transactionParams);
        return new LightrailActionTransaction(transaction);
    }

    public static GiftCard createWithStartAndExpiryDate(String programId, int initialValue, String startDate, String expiryDate) throws AuthorizationException, CouldNotFindObjectException, IOException {
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
        return createWithStartAndExpiryDate(programId, initialValue, null,null);
    }

    public static GiftCard create(Map<String, Object> cardCreationParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.PROGRAM_ID
        ), cardCreationParams);

        cardCreationParams = LightrailTransaction.addDefaultIdempotencyKeyIfNotProvided(cardCreationParams);
        cardCreationParams.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_GIFT_CARD);

        Card card = APICore.createCard(cardCreationParams);
        return new GiftCard(card);
    }

    public static GiftCard retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return new GiftCard(APICore.retrieveCard(cardId));
    }
}

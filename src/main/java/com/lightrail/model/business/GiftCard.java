package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.Card;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiftCard {
    Card card;

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

        cardCreationParams.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_GIFT_CARD);
        String idempotencyKey = (String) cardCreationParams.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);
        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            cardCreationParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, idempotencyKey);
        }

        Card card = APICore.createCard(cardCreationParams);
        return new GiftCard(card);
    }

    public static GiftCard retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return new GiftCard(APICore.retrieveCard(cardId));
    }
}

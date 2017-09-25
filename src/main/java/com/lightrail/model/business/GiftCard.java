package com.lightrail.model.business;


import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.CardDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GiftCard extends LightrailCard {
    public GiftCard (String jsonObject) {
        super(jsonObject);
    }

    public GiftCard (Card card) {
        super(card.getRawJson());
    }

    public String retrieveFullCode() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveCardsFullCode(getCardId()).getCode();
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

    public static GiftCard create(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
//        LightrailConstants.Parameters.requireParameters(Arrays.asList(
//        ), params);

        params = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(params);
        params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_GIFT_CARD);

        return new GiftCard(LightrailCard.create(params));
    }

    public static GiftCard retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Card card = LightrailCard.retrieve(cardId);
        if (LightrailConstants.Parameters.CARD_TYPE_GIFT_CARD.equals(card.getCardType()))
            return new GiftCard(card);
        else
            throw new CouldNotFindObjectException("This cardId is not associated with an Gift Card.");
    }

    public static CardDetails retrieveCardDetailsByCode (String code) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveCardDetailsByCode(code);
    }
}

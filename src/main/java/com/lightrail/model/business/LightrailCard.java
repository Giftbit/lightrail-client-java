package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

class LightrailCard {

    public static Card retrieve (String cardId, Class<? extends Card> cardClass) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.retrieveCard(cardId, cardClass);
    }

    public static Card createAccountCard(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.CONTACT_ID, LightrailConstants.Parameters.CURRENCY
        ), params);

        params = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(params);
        params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);

        return create(params, AccountCard.class);
    }

    public static Card createGiftCard(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.PROGRAM_ID
        ), params);

        params = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(params);
        params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_GIFT_CARD);

        return create(params, GiftCard.class);
    }

    private static Card create(Map<String, Object> params, Class<? extends Card> cardClass) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.USER_SUPPLIED_ID
        ), params);

        return APICore.createCard(params, cardClass);
    }
}

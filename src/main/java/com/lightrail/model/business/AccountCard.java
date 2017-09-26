package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class AccountCard extends LightrailCard {
    public AccountCard(String jsonObject) {
        super(jsonObject);
    }

    public AccountCard(Card card) {
        super(card.getRawJson());
    }

    public static AccountCard retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Card card = LightrailCard.retrieve(cardId);
        if (LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD.equals(card.getCardType()))
            return new AccountCard(card);
        else
            throw new CouldNotFindObjectException("This cardId is not associated with an Account Card.");
    }

    public static AccountCard create(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.CONTACT_ID, LightrailConstants.Parameters.CURRENCY
        ), params);

        params = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(params);
        params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);

        return new AccountCard(LightrailCard.create(params));
    }

}

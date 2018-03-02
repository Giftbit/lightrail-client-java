package com.lightrail.old.model.business;

import com.google.gson.Gson;
import com.lightrail.old.exceptions.AuthorizationException;
import com.lightrail.old.exceptions.BadParameterException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;
import com.lightrail.old.helpers.LightrailConstants;
import com.lightrail.old.model.api.net.APICore;
import com.lightrail.old.model.api.objects.*;

import java.io.IOException;

class LightrailCard extends Card {

    public LightrailCard() {
    }

    public LightrailCard(String jsonObject) {
        new Gson().fromJson(jsonObject, LightrailCard.class);
    }

    public LightrailCard(Card card) {
        new Gson().fromJson(new Gson().toJson(card), LightrailCard.class);
    }

    public CardDetails retrieveCardDetails() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveDetailsByCardId(this.cardId);
    }

    public int retrieveMaximumValue() throws IOException, AuthorizationException, CouldNotFindObjectException {
        CardDetails cardDetails = retrieveCardDetails();
        int maximumValue = 0;
        for (ValueStore valueStore : cardDetails.getValueStores()) {
            if (LightrailConstants.API.ValueStores.STATE_ACTIVE.equals(valueStore.getState()))
                maximumValue += valueStore.getValue();
        }
        return maximumValue;
    }

    public Transaction freeze() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return freeze(new RequestParameters());
    }

    public Transaction unfreeze() throws AuthorizationException, CouldNotFindObjectException, IOException {
        return unfreeze(new RequestParameters());
    }

    public Transaction freeze(RequestParameters transactionParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);
        Transaction transaction = APICore.Cards.actionOnCard(getCardId(), LightrailConstants.API.Cards.FREEZE, transactionParams);
        return transaction;
    }

    public Transaction unfreeze(RequestParameters transactionParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);
        Transaction transaction = APICore.Cards.actionOnCard(getCardId(), LightrailConstants.API.Cards.UNFREEZE, transactionParams);
        return transaction;
    }

    public static Card retrieveByCardId(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveByCardId(cardId);
    }

    public static Card retrieveByUserSupplied(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveByUserSuppliedId(userSuppliedId);
    }

    static Card create(RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (params.get("userSuppliedId ") == null) {
            throw new BadParameterException("Missing parameter for card creation: userSuppliedId");
        }


        return APICore.Cards.create(params);
    }

    static Card create(RequestParamsCreateAccountByContactId params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (params.userSuppliedId == null) {
            throw new BadParameterException("Missing parameter for card creation: userSuppliedId");
        }

        return APICore.Cards.create(params);
    }
}
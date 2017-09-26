package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.*;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class LightrailCard extends Card {

    public LightrailCard(String jsonObject) {
        super(jsonObject);
    }

    public CardDetails retrieveCardDetails () throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveCardDetailsByCardId(this.cardId);
    }

    public int retrieveMaximumValue () throws IOException, AuthorizationException, CouldNotFindObjectException {
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

    public static Card retrieve(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return APICore.Cards.retrieveCard(cardId);
    }

    static Card create(RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.USER_SUPPLIED_ID
        ), params);

        return APICore.Cards.createCard(params);
    }
}

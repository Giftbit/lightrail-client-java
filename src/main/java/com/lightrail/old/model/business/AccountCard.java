package com.lightrail.old.model.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lightrail.old.exceptions.AuthorizationException;
import com.lightrail.old.exceptions.BadParameterException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;
import com.lightrail.old.helpers.LightrailConstants;
import com.lightrail.old.model.api.net.APICore;
import com.lightrail.old.model.api.objects.Card;
import com.lightrail.old.model.api.objects.RequestParamsCreateAccountByContactId;
import com.lightrail.old.model.api.objects.RequestParamsCreateAccountByShopperId;

import java.io.IOException;

public class AccountCard extends LightrailCard {

    public AccountCard() {
        super();
    }

    public AccountCard(String jsonObject) {
        super(jsonObject);
    }

    public AccountCard(Card card) {
        super(card.getRawJson());
    }


    public static AccountCard create(RequestParamsCreateAccountByContactId params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (params == null) {
            throw new BadParameterException("Cannot create Account with params: null");
        }
        if (params.contactId == null) {
            throw new BadParameterException("Missing parameter for account creation: contactId");
        }
        if (params.currency == null) {
            throw new BadParameterException("Missing parameter for account creation: currency");
        }
        if (params.userSuppliedId == null) {
            throw new BadParameterException("Missing parameter for account creation: userSuppliedId");
        }

        LightrailContact contact = LightrailContact.retrieve(params.contactId);

        if (contact != null) {
            try {
                return retrieveByContactIdAndCurrency(contact.contactId, params.currency);
            } catch (CouldNotFindObjectException ignored) {
                // if the account doesn't exist yet, that's fine, the next step creates one
            }
            params.cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
            return new AccountCard(LightrailCard.create(params));
        } else {
            throw new BadParameterException("Could not find the Contact for that contactId: " + params.contactId);
        }

    }

    public static AccountCard create(RequestParamsCreateAccountByShopperId params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (params == null) {
            throw new BadParameterException("Cannot create Account with params: null");
        }
        if (params.shopperId == null) {
            throw new BadParameterException("Missing parameter for account creation: shopperId");
        }
        if (params.currency == null) {
            throw new BadParameterException("Missing parameter for account creation: currency");
        }
        if (params.userSuppliedId == null) {
            throw new BadParameterException("Missing parameter for account creation: userSuppliedId");
        }


        LightrailContact contact;
        try {
            contact = LightrailContact.retrieveByUserSuppliedId(params.shopperId);
        } catch (CouldNotFindObjectException ignored) {
            JsonObject jsonContactParams = new Gson().fromJson("{\"userSuppliedId\":\"" + params.shopperId + "\"}", JsonObject.class);

            RequestParametersCreateContact contactParams = new RequestParametersCreateContact(jsonContactParams.toString());
            contact = LightrailContact.create(contactParams);
        }

        try {
            return retrieveByContactIdAndCurrency(contact.contactId, params.currency);
        } catch (CouldNotFindObjectException ignored) {
            RequestParamsCreateAccountByContactId contactIdParams = new RequestParamsCreateAccountByContactId(params, contact.contactId);
            Card card = LightrailCard.create(contactIdParams);
            return new Gson().fromJson(new Gson().toJson(card), AccountCard.class);
        }

    }

    public static AccountCard retrieveByContactIdAndCurrency(String contactId, String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return (AccountCard) APICore.Cards.retrieveAccountCardByContactIdAndCurrency(contactId, currency);
    }


    public static AccountCard retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Card card = retrieveByUserSupplied(userSuppliedId);
        makeSureCardTypeIsCorrect(card);
        return new AccountCard(card);
    }

    public static AccountCard retrieveByCardId(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Card card = LightrailCard.retrieveByCardId(cardId);
        makeSureCardTypeIsCorrect(card);
        return new AccountCard(card);
    }

    private static void makeSureCardTypeIsCorrect(Card card) throws CouldNotFindObjectException {
        if (!LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD.equals(card.getCardType()))
            throw new CouldNotFindObjectException("This cardId is not associated with a Account Card.");
    }

}

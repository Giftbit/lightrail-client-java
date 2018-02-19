package com.lightrail.model.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.RequestParamsCreateAccountByContactId;
import com.lightrail.model.api.objects.RequestParamsCreateAccountByShopperId;

import java.io.IOException;

public class AccountCard extends LightrailCard {
    public AccountCard(String jsonObject) {
        super(jsonObject);
    }

    public AccountCard(Card card) {
        super(card.getRawJson());
    }

//    RE-WRITING THIS METHOD TO USE DTO TO VERIFY PARAMS: must add contactId, currency, userSuppliedId; cardType & initialValue have sensible defaults
//    TODO: ensure all checks/replacements still work as planned
//
//    public static AccountCard create(RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
//        LightrailConstants.Parameters.requireParameters(Arrays.asList(
//                LightrailConstants.Parameters.CONTACT_ID, LightrailConstants.Parameters.CURRENCY
//        ), params);
//
//        LightrailContact contact = LightrailContact.retrieve(params.get("contactId").toString());
//
//        AccountCard account = null;
//        if (contact != null) {
//            try {
//                account = retrieveByContactIdAndCurrency(contact.contactId, params.get("currency").toString());
//                return account;
//            } catch (CouldNotFindObjectException e) {
//                account = null;
//            } catch (BadParameterException e) {
//                account = null;
//            }
//        }
//
//        params = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(params);
//        params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);
//
//        return new AccountCard(LightrailCard.create(params));
//    }

    public static AccountCard create(RequestParamsCreateAccountByContactId params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailContact contact = LightrailContact.retrieve(params.contactId.toString());

        AccountCard account = null;
        if (contact != null) {
            try {
                account = retrieveByContactIdAndCurrency(contact.contactId, params.currency.toString());
                return account;
            } catch (CouldNotFindObjectException e) {
                account = null;
            } catch (BadParameterException e) {
                account = null;
            }
        }

        params.cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;

        return new AccountCard(LightrailCard.create(params));
    }

    public static AccountCard create(RequestParamsCreateAccountByShopperId params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailContact contact;
        try {
            contact = LightrailContact.retrieveByUserSuppliedId(params.shopperId);
        } catch (CouldNotFindObjectException ignored) {
            JsonObject jsonContactParams = new Gson().fromJson("{\"userSuppliedId\":\"" + params.shopperId + "\"}", JsonObject.class);

            RequestParametersCreateContact contactParams = new RequestParametersCreateContact(jsonContactParams.toString());
            contact = LightrailContact.create(contactParams);
        }
        String jsonStringParams = new Gson().toJson(params);
        JsonObject jsonParams = new Gson().fromJson(jsonStringParams, JsonObject.class);
        jsonParams.add("contactId", new JsonPrimitive(contact.contactId));

        try {
            return retrieveByContactIdAndCurrency(contact.contactId, jsonParams.get("currency").getAsString());
        } catch (CouldNotFindObjectException ignored) {
            RequestParamsCreateAccountByContactId contactIdParams = new RequestParamsCreateAccountByContactId(jsonParams.get("rawJson").getAsString());

            return new AccountCard(LightrailCard.create(contactIdParams));
        }

    }

    public static AccountCard retrieveByContactIdAndCurrency(String contactId, String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return (AccountCard) APICore.Cards.retrieveAccountCardByContactIdAndCurrency(contactId, currency);
    }


    public static AccountCard retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Card card = LightrailCard.retrieveByUserSupplied(userSuppliedId);
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

package com.lightrail;

import com.lightrail.model.*;
import com.lightrail.params.*;

import java.util.ArrayList;

import static java.lang.String.format;

public class Accounts {
    private final LightrailClient lr;

    public Accounts(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateAccountCardByContactIdParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot create Account with params: null");
        }
        if (params.contactId == null) {
            throw new LightrailException("Missing parameter for account creation: contactId");
        }
        if (params.currency == null) {
            throw new LightrailException("Missing parameter for account creation: currency");
        }
        if (params.userSuppliedId == null) {
            throw new LightrailException("Missing parameter for account creation: userSuppliedId");
        }

        Contact contact = lr.contacts.retrieve(params.contactId);
        if (contact == null) {
            throw new LightrailException("Could not find the Contact for that contactId: " + params.contactId);
        }


        Card card = retrieveByContactIdAndCurrency(contact.contactId, params.currency);
        if (card != null) {
            return card;
        }

        CreateCardParams newCardParams = new CreateCardParams();
        newCardParams.currency = params.currency;
        newCardParams.userSuppliedId = params.userSuppliedId;
        newCardParams.contactId = params.contactId;
        newCardParams.initialValue = params.initialValue;
        newCardParams.cardType = "ACCOUNT_CARD";

        return lr.cards.create(newCardParams);
    }

    public Card create(CreateAccountCardByShopperIdParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot create Account with params: null");
        }
        if (params.shopperId == null) {
            throw new LightrailException("Missing parameter for account creation: shopperId");
        }
        if (params.currency == null) {
            throw new LightrailException("Missing parameter for account creation: currency");
        }
        if (params.userSuppliedId == null) {
            throw new LightrailException("Missing parameter for account creation: userSuppliedId");
        }

        Contact contact = lr.contacts.retrieveByUserSuppliedId(params.shopperId);
        if (contact == null) {
            contact = lr.contacts.create(params.shopperId);
        }

        Card card = retrieveByContactIdAndCurrency(contact.contactId, params.currency);
        if (card != null) {
            return card;
        }

        CreateCardParams newCardParams = new CreateCardParams();
        newCardParams.currency = params.currency;
        newCardParams.userSuppliedId = params.userSuppliedId;
        newCardParams.contactId = contact.contactId;
        newCardParams.initialValue = params.initialValue;
        newCardParams.cardType = "ACCOUNT_CARD";

        return lr.cards.create(newCardParams);
    }

    public Card retrieveByContactIdAndCurrency(String contactId, String currency) throws LightrailException {
        ArrayList<Card> cards = lr.cards.retrieve(buildAccountSearchParams(contactId, currency));
        if (cards == null || cards.size() == 0) {
            return null;
        }
        return cards.get(0);
    }

    public Card retrieveByShopperIdAndCurrency(String shopperId, String currency) throws LightrailException {
        Contact contact = lr.contacts.retrieveByShopperId(shopperId);
        if (contact == null) {
            return null;
        }
        return retrieveByContactIdAndCurrency(contact.contactId, currency);
    }

    public CardDetails getDetailsByContactIdAndCurrency(String contactId, String currency) throws LightrailException {
        Card card = retrieveByContactIdAndCurrency(contactId, currency);
        return lr.cards.getDetails(card.cardId);
    }

    public CardDetails getDetailsByShopperIdAndCurrency(String shopperId, String currency) throws LightrailException {
        Card card = retrieveByShopperIdAndCurrency(shopperId, currency);
        return lr.cards.getDetails(card.cardId);
    }

    public Transaction createTransaction(CreateAccountTransactionByContactIdParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot create account transaction with params: null");
        }
        if (params.contactId == null) {
            throw new LightrailException("Missing parameter for account transaction: contactId");
        }
        if (params.currency == null) {
            throw new LightrailException("Missing parameter for account transaction: currency");
        }
        if (params.userSuppliedId == null) {
            throw new LightrailException("Missing parameter for account transaction: userSuppliedId");
        }

        Card card = retrieveByContactIdAndCurrency(params.contactId, params.currency);
        if (card == null || card.cardId == null) {
            throw new LightrailException(format("Could not find account card for contact '%s' with currency '%s'", params.contactId, params.currency));
        }

        CreateTransactionParams cardParams = new CreateTransactionParams(params, card.cardId);
        return lr.cards.createTransaction(cardParams);
    }

    public Transaction createTransaction(CreateAccountTransactionByShopperIdParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot create account transaction with params: null");
        }
        if (params.shopperId == null) {
            throw new LightrailException("Missing parameter for account transaction: contactId");
        }
        if (params.currency == null) {
            throw new LightrailException("Missing parameter for account transaction: currency");
        }
        if (params.userSuppliedId == null) {
            throw new LightrailException("Missing parameter for account transaction: userSuppliedId");
        }

        Contact contact = lr.contacts.retrieveByUserSuppliedId(params.shopperId);
        if (contact == null || contact.contactId == null) {
            throw new LightrailException(format("Could not find contact for shopperId '%s'", params.shopperId));
        }

        CreateAccountTransactionByContactIdParams contactParams = new CreateAccountTransactionByContactIdParams(params, contact.contactId);
        return createTransaction(contactParams);
    }

    public Transaction capturePendingTransaction(HandleAccountPendingByContactId params) throws LightrailException {
        String cardId = retrieveByContactIdAndCurrency(params.contactId, params.currency).cardId;
        CompletePendingTransactionParams fullParams = new CompletePendingTransactionParams(params, cardId, true);
        return lr.cards.completePendingTransaction(fullParams);
    }

    public Transaction capturePendingTransaction(HandleAccountPendingByShopperId params) throws LightrailException {
        String cardId = retrieveByShopperIdAndCurrency(params.shopperId, params.currency).cardId;
        CompletePendingTransactionParams fullParams = new CompletePendingTransactionParams(params, cardId, true);
        return lr.cards.completePendingTransaction(fullParams);
    }

    public Transaction voidPendingTransaction(HandleAccountPendingByContactId params) throws LightrailException {
        String cardId = retrieveByContactIdAndCurrency(params.contactId, params.currency).cardId;
        CompletePendingTransactionParams fullParams = new CompletePendingTransactionParams(params, cardId, false);
        return lr.cards.completePendingTransaction(fullParams);
    }

    public Transaction voidPendingTransaction(HandleAccountPendingByShopperId params) throws LightrailException {
        String cardId = retrieveByShopperIdAndCurrency(params.shopperId, params.currency).cardId;
        CompletePendingTransactionParams fullParams = new CompletePendingTransactionParams(params, cardId, false);
        return lr.cards.completePendingTransaction(fullParams);
    }


    private CardSearchParams buildAccountSearchParams(String contactId, String currency) {
        CardSearchParams cardSearchParams = new CardSearchParams();
        cardSearchParams.cardType = "ACCOUNT_CARD";
        cardSearchParams.currency = currency;
        cardSearchParams.contactId = contactId;
        return cardSearchParams;
    }
}

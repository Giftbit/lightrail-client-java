package com.lightrail;

import com.lightrail.model.Card;
import com.lightrail.model.Contact;
import com.lightrail.model.LightrailException;
import com.lightrail.model.Transaction;
import com.lightrail.params.*;
import com.lightrail.utils.LightrailConstants;

import java.io.IOException;

import static java.lang.String.format;

public class Accounts {
    public LightrailClient lr;

    public Accounts(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateAccountCardByContactIdParams params) throws LightrailException, IOException {
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

        Card card = lr.cards.retrieveAccountCardByContactIdAndCurrency(contact.contactId, params.currency);
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

    public Card create(CreateAccountCardByShopperIdParams params) throws LightrailException, IOException {
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

        Card card = lr.cards.retrieveAccountCardByContactIdAndCurrency(contact.contactId, params.currency);
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

    public Card retrieveByContactIdAndCurrency(String contactId, String currency) throws IOException, LightrailException {
        return lr.cards.retrieveAccountCardByContactIdAndCurrency(contactId, currency);
    }

    public Card retrieveByShopperIdAndCurrency(String shopperId, String currency) throws IOException, LightrailException {
        Contact contact = lr.contacts.retrieveByShopperId(shopperId);
        if (contact == null) {
            return null;
        }
        String contactId = contact.contactId;
        return lr.cards.retrieveAccountCardByContactIdAndCurrency(contactId, currency);
    }

    public Transaction createTransaction(CreateAccountTransactionByContactIdParams params) throws LightrailException, IOException {
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

        Card card = lr.cards.retrieveAccountCardByContactIdAndCurrency(params.contactId, params.currency);
        if (card == null || card.cardId == null) {
            throw new LightrailException(format("Could not find account card for contact '%s' with currency '%s'", params.contactId, params.currency));
        }

        CreateTransactionParams cardParams = new CreateTransactionParams(params, card.cardId);
        return lr.cards.createTransaction(cardParams);
    }

    public Transaction createTransaction(CreateAccountTransactionByShopperIdParams params) throws LightrailException, IOException {
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
}

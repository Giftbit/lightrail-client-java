package com.lightrail;

import com.lightrail.model.Card;
import com.lightrail.model.Contact;
import com.lightrail.model.LightrailException;
import com.lightrail.params.CreateAccountCardByContactIdParams;
import com.lightrail.params.CreateAccountCardByShopperIdParams;
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
        if (params.cardType == null) {
            params.cardType = "ACCOUNT_CARD";
        } else if (params.cardType != "ACCOUNT_CARD") {
            throw new LightrailException(format("Cannot create account with cardType '%s'", params.cardType));
        }

        Contact contact = lr.contacts.retrieve(params.contactId);

        if (contact != null) {
            try {
                return lr.cards.retrieveAccountCardByContactIdAndCurrency(contact.contactId, params.currency);
            } catch (LightrailException ignored) {
                // if the account doesn't exist yet, that's fine, the next step creates one
            }
            params.cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
            return lr.cards.create(params);
        } else {
            throw new LightrailException("Could not find the Contact for that contactId: " + params.contactId);
        }
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
        if (params.cardType == null) {
            params.cardType = "ACCOUNT_CARD";
        } else if (params.cardType != "ACCOUNT_CARD") {
            throw new LightrailException(format("Cannot create account with cardType '%s'", params.cardType));
        }

        Contact contact;
        try {
            contact = lr.contacts.retrieveByUserSuppliedId(params.shopperId);
        } catch (LightrailException ignored) {
            contact = lr.contacts.create(params.shopperId);
        }

        CreateAccountCardByContactIdParams contactIdParams = new CreateAccountCardByContactIdParams(params, contact.contactId);
        return create(contactIdParams);
    }


}

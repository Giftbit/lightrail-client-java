package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.CardSearchResult;
import com.lightrail.model.api.objects.Contact;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.RequestParameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LightrailContact extends Contact {
    private Map<String, AccountCard> cardForCurrency = new HashMap<>();

    public LightrailContact(String jsonObject) {
        super(jsonObject);
    }

    public LightrailContact(Contact contact) {
        super(contact.getRawJson());
    }

    public Collection<String> getAvailableCurrencies() {
        return cardForCurrency.keySet();
    }

    private String getDefaultCurrency() {
        if (cardForCurrency.keySet().size() == 1) {
            return cardForCurrency.keySet().iterator().next();
        } else {
            throw new BadParameterException("Need to specify the currency.");
        }
    }

    public String getShopperId() {
        return getUserSuppliedId();
    }

    private void loadCard(AccountCard card) {
        cardForCurrency.put(card.getCurrency(), card);
    }

    public LightrailContact addCurrency(String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return addCurrency(currency, 0);
    }

    public LightrailContact addCurrency(String currency, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        RequestParameters cardParams = new RequestParameters();
        cardParams.put(LightrailConstants.Parameters.CONTACT_ID, getContactId());
        cardParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        cardParams.put(LightrailConstants.Parameters.INITIAL_VALUE, initialValue);
        cardParams.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);
        return addCurrency(cardParams);
    }

    public LightrailContact addCurrency(RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.CURRENCY
        ), params);

        String cardType = (String) params.get(LightrailConstants.Parameters.CARD_TYPE);
        if (cardType == null)
            params.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);
        else if (! LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD.equals(cardType))
            throw new BadParameterException(String.format("Card Type must be set to ACCOUNT_CARD for creating a new account card. (Given: %s).",cardType));

        String currency = (String) params.get(LightrailConstants.Parameters.CURRENCY);
        AccountCard card = AccountCard.create(params);
        cardForCurrency.put(currency, card);
        return this;
    }

    public LightrailTransaction createPendingTransaction(int value) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return createPendingTransaction(value, getDefaultCurrency());
    }

    public LightrailTransaction createPendingTransaction(int value, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return createTransaction(value, currency, true);
    }

    public LightrailTransaction createTransaction(int value) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return createTransaction(value, getDefaultCurrency());
    }

    public LightrailTransaction createTransaction(int value, boolean pending) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return createTransaction(value, getDefaultCurrency(), pending);
    }

    public LightrailTransaction createTransaction(int value, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return createTransaction(value, currency, false);
    }

    public LightrailTransaction createTransaction(int value, String currency, boolean pending) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        RequestParameters transactionParams = new RequestParameters();
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
        return createTransaction(transactionParams);
    }

    public LightrailTransaction createTransaction(RequestParameters transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.VALUE,
                LightrailConstants.Parameters.CURRENCY
        ), transactionParams);

        String currency = (String) transactionParams.get(LightrailConstants.Parameters.CURRENCY);
        AccountCard cardObject = cardForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account. ", currency));
        String cardId = cardObject.getCardId();

        transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);

        return LightrailTransaction.Create.create(transactionParams);
    }

    public int retrieveMaximumValue() throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return retrieveMaximumValue(getDefaultCurrency());
    }

    public int retrieveMaximumValue (String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        return getCardFor(currency).retrieveMaximumValue();
    }

    public AccountCard getCardFor(String currency) {
        AccountCard cardObject = cardForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account.", currency));
        else
            return cardObject;
    }

    public static LightrailContact create(String email, String firstName, String lastName, String defaultCurrency, int initialBalance) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return create(email, firstName, lastName).
                addCurrency(defaultCurrency, initialBalance);
    }

    public static LightrailContact create(String email, String firstName, String lastName) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (email == null || email.isEmpty())
            throw new BadParameterException("Need to provide an email address for the new LightrailContact.");

        RequestParameters customerAccountParams = new RequestParameters();
        customerAccountParams.put(LightrailConstants.Parameters.EMAIL, email);
        if (firstName != null)
            customerAccountParams.put(LightrailConstants.Parameters.FIRST_NAME, firstName);
        if (lastName != null)
            customerAccountParams.put(LightrailConstants.Parameters.LAST_NAME, lastName);

        return create(customerAccountParams);
    }

    public static LightrailContact create(RequestParameters customerAccountParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.EMAIL)
                , customerAccountParams);

        customerAccountParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(customerAccountParams);
        Contact contactObject = APICore.Contacts.create(customerAccountParams);
        return new LightrailContact(contactObject);
    }

    private static void loadCards(LightrailContact contact) throws AuthorizationException, CouldNotFindObjectException, IOException {
        CardSearchResult cards = APICore.Cards.retrieveCardsOfContact(contact.getContactId());
        for (Card card : cards.getCards()) {
            if (LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD.equals(card.getCardType()))
                contact.loadCard(new AccountCard(card));
        }
    }

    public static LightrailContact retrieve(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.Contacts.retrieve(customerAccountId);
        LightrailContact customerAccount = new LightrailContact(contactObject);
//        loadCards(customerAccount);   // todo this should be extracted - doesn't need to be part of default get-contact flow
        return customerAccount;
    }

    public static LightrailContact retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.Contacts.retrieveByUserSuppliedId(userSuppliedId);
        LightrailContact customerAccount = new LightrailContact(contactObject);
//        loadCards(customerAccount);   // todo this should be extracted - doesn't need to be part of default get-contact flow
        return customerAccount;
    }

    private static void cancelCard(String cardId, String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        RequestParameters params = new RequestParameters();
        params.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);

        APICore.Cards.cancel(cardId, params);
    }

    public static RequestParameters handleContact(RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        RequestParameters chargeParamsCopy = new RequestParameters(params);

        String contactId = (String) chargeParamsCopy.remove(LightrailConstants.Parameters.CONTACT);
        String shopperId = (String) chargeParamsCopy.remove(LightrailConstants.Parameters.SHOPPER_ID);
        String requestedCurrency = (String) chargeParamsCopy.get(LightrailConstants.Parameters.CURRENCY);

        if (contactId != null || shopperId != null) {
            if (requestedCurrency != null && !requestedCurrency.isEmpty()) {
                LightrailContact account;
                if (contactId != null) {
                    account = retrieve(contactId);
                } else {
                    account = retrieveByUserSuppliedId(shopperId);
                }
                String cardId = account.getCardFor(requestedCurrency).getCardId();
                chargeParamsCopy.put(LightrailConstants.Parameters.CARD_ID, cardId);
            } else {
                throw new BadParameterException("Must provide a valid 'currency' when using 'contact'.");
            }
        }
        return chargeParamsCopy;
    }

}

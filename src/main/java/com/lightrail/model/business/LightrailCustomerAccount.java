package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.CardSearchResult;
import com.lightrail.model.api.objects.Contact;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.*;

public class LightrailCustomerAccount extends Contact {
    private Map<String, AccountCard> cardForCurrency = new HashMap<>();

    public LightrailCustomerAccount(String jsonObject) {
        super(jsonObject);
    }

    public LightrailCustomerAccount(Contact contact) {
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

    private void setCard (AccountCard card) {
        cardForCurrency.put(card.getCurrency(), card);
    }

    public LightrailCustomerAccount addCurrency(String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return addCurrency(currency, 0);
    }

    public LightrailCustomerAccount addCurrency(String currency, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put(LightrailConstants.Parameters.CONTACT_ID, getContactId());
        cardParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        cardParams.put(LightrailConstants.Parameters.INITIAL_VALUE, initialValue);
        cardParams.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);
        return addCurrency(cardParams);
    }

    public LightrailCustomerAccount addCurrency(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
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
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
        return createTransaction(transactionParams);
    }

    public LightrailTransaction createTransaction(Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
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

        return LightrailTransaction.create(transactionParams);
    }

    public int retrieveMaximumValue() throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return retrieveMaximumValue(getDefaultCurrency());
    }

    public int retrieveMaximumValue (String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        return getCardFor(currency).retrieveMaximumValue();
    }

    AccountCard getCardFor(String currency) {
        AccountCard cardObject = cardForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account.", currency));
        else
            return cardObject;
    }

    public static LightrailCustomerAccount create(String email, String firstName, String lastName, String defaultCurrency, int initialBalance) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return create(email, firstName, lastName).
                addCurrency(defaultCurrency, initialBalance);
    }

    public static LightrailCustomerAccount create(String email, String firstName, String lastName) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (email == null || email.isEmpty())
            throw new BadParameterException("Need to provide an email address for the new LightrailCustomerAccount.");

        Map<String, Object> customerAccountParams = new HashMap<>();
        customerAccountParams.put(LightrailConstants.Parameters.EMAIL, email);
        if (firstName != null)
            customerAccountParams.put(LightrailConstants.Parameters.FIRST_NAME, firstName);
        if (lastName != null)
            customerAccountParams.put(LightrailConstants.Parameters.LAST_NAME, lastName);

        return create(customerAccountParams);
    }

    public static LightrailCustomerAccount create(Map<String, Object> customerAccountParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.EMAIL)
                , customerAccountParams);

        customerAccountParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(customerAccountParams);
        Contact contactObject = APICore.Contact.createContact(customerAccountParams);
        return new LightrailCustomerAccount(contactObject);
    }

    public static LightrailCustomerAccount retrieve(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.Contact.retrieveContact(customerAccountId);
        LightrailCustomerAccount customerAccount = new LightrailCustomerAccount(contactObject);

        CardSearchResult cards = APICore.Cards.retrieveCardsOfContact(customerAccountId);
        for (Card card : cards.getCards()) {
            if (LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD.equals(card.getCardType()))
                customerAccount.setCard(new AccountCard(card));
        }
        return customerAccount;
    }

    private static void cancelCard(String cardId, String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);

        APICore.Cards.cancelCard(cardId, params);
    }

    public static Map<String, Object> handleContact(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> chargeParamsCopy = new HashMap<>(params);

        String contactId = (String) chargeParamsCopy.remove(LightrailConstants.Parameters.CONTACT);
        String requestedCurrency = (String) chargeParamsCopy.get(LightrailConstants.Parameters.CURRENCY);

        if (contactId != null) {
            if (requestedCurrency != null && !requestedCurrency.isEmpty()) {
                LightrailCustomerAccount account = retrieve(contactId);
                String cardId = account.getCardFor(requestedCurrency).getCardId();
                chargeParamsCopy.put(LightrailConstants.Parameters.CARD_ID, cardId);
            } else {
                throw new BadParameterException("Must provide a valid 'currency' when using 'contact'.");
            }
        }
        return chargeParamsCopy;
    }

}

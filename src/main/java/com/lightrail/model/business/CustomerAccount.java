package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Card;
import com.lightrail.model.api.objects.CardSearchResult;
import com.lightrail.model.api.objects.Contact;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.*;

public class CustomerAccount {
    private Map<String, AccountCard> cardIdForCurrency = new HashMap<>();

    Contact contactObject;

    public String getId() {
        return contactObject.getContactId();
    }

    public String getEmail() {
        return contactObject.getEmail();
    }

    public String getFirstName() {
        return contactObject.getFirstName();
    }

    public String getLastName() {
        return contactObject.getLastName();
    }

    public Collection<String> getAvailableCurrencies() {
        return cardIdForCurrency.keySet();
    }

    private CustomerAccount(Contact contactObject) {
        this.contactObject = contactObject;
    }

    private CustomerAccount(Contact contactObject, List<AccountCard> cards) {
        this.contactObject = contactObject;
        for (AccountCard card : cards)
            cardIdForCurrency.put(card.getCurrency(), card);
    }

    private String getDefaultCurrency() {
        if (cardIdForCurrency.keySet().size() == 1) {
            return cardIdForCurrency.keySet().iterator().next();
        } else {
            throw new BadParameterException("Need to specify the currency.");
        }
    }

    public CustomerAccount addCurrency(String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return addCurrency(currency, 0);
    }
    public CustomerAccount addCurrency(String currency, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put(LightrailConstants.Parameters.CONTACT_ID, getId());
        cardParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        cardParams.put(LightrailConstants.Parameters.INITIAL_VALUE, initialValue);
        cardParams.put(LightrailConstants.Parameters.CARD_TYPE, LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD);
        return addCurrency(cardParams);
    }

    public CustomerAccount addCurrency(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.CURRENCY
        ), params);

        String currency = (String) params.get(LightrailConstants.Parameters.CURRENCY);
        AccountCard card = (AccountCard) LightrailCard.createAccountCard(params);
        cardIdForCurrency.put(currency, card);
        return this;
    }

    public LightrailTransaction pendingTransact (int value) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return pendingTransact(value, getDefaultCurrency());
    }

    public LightrailTransaction pendingTransact(int value, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return transact(value,  currency, true);
    }

    public LightrailTransaction transact (int value) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return transact (value, getDefaultCurrency());
    }

    public LightrailTransaction transact (int value, boolean pending) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return transact(value, getDefaultCurrency(), pending);
    }

    public LightrailTransaction transact(int value, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return transact( value,  currency, false);
    }

    public LightrailTransaction transact (int value, String currency, boolean pending) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
        return transact (transactionParams);
    }

    public LightrailTransaction transact (Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.VALUE,
                LightrailConstants.Parameters.CURRENCY
        ), transactionParams);

        String currency = (String) transactionParams.get(LightrailConstants.Parameters.CURRENCY);
        Card cardObject = cardIdForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account. ", currency));
        String cardId = cardObject.getCardId();

        transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);

        return LightrailTransaction.create(transactionParams);
    }

    public LightrailValue balance () throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        getCardFor(getDefaultCurrency());
        return balance(getDefaultCurrency());
    }

    public LightrailValue balance (String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        return LightrailValue.retrieveByCardId(getCardFor(currency).getCardId());
    }

    Card getCardFor(String currency) {
        Card cardObject = cardIdForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account.", currency));
        else
            return cardObject;
    }

    public static CustomerAccount create(String email, String firstName, String lastName, String defaultCurrency, int initialBalance) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return create(email, firstName, lastName).
                addCurrency(defaultCurrency,initialBalance);
    }
    public static CustomerAccount create(String email, String firstName, String lastName) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (email == null || email.isEmpty())
            throw new BadParameterException("Need to provide an email address for the new CustomerAccount.");

        Map<String, Object> customerAccountParams = new HashMap<>();
        customerAccountParams.put(LightrailConstants.Parameters.EMAIL, email);
        if (firstName != null)
            customerAccountParams.put(LightrailConstants.Parameters.FIRST_NAME, firstName);
        if (lastName != null)
            customerAccountParams.put(LightrailConstants.Parameters.LAST_NAME, lastName);

        return create(customerAccountParams);
    }

    public static CustomerAccount create(Map<String, Object> customerAccountParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(LightrailConstants.Parameters.EMAIL), customerAccountParams);

        customerAccountParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(customerAccountParams);
        Contact contactObject = APICore.createContact(customerAccountParams);
        return new CustomerAccount(contactObject);
    }

    public static CustomerAccount retrieve(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.retrieveContact(customerAccountId);
        CardSearchResult cards = APICore.retrieveCardsOfContact(customerAccountId);
        CustomerAccount customerAccount = new CustomerAccount(contactObject, cards.getCards());
        return customerAccount;
    }

    private static void cancelCard(String cardId, String idempotencyKey) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, idempotencyKey);

        APICore.cancelCard(cardId, params);
    }

//    public static void delete(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
//        CustomerAccount customerAccount = retrieve(customerAccountId);
//        for (String currency : customerAccount.getAvailableCurrencies()) {
//            Card card = customerAccount.getCardFor(currency);
//            String idempotencyKey = card.getUserSuppliedId() + "-cancel";
//            String cardId = card.getCardId();
//            cancelCard(cardId, idempotencyKey);
//        }
//        APICore.deleteContact(customerAccountId);
//    }
}

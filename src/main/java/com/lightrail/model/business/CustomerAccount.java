package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Card;
import com.lightrail.model.api.CardSearchResult;
import com.lightrail.model.api.Contact;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class CustomerAccount {
    private Map<String, Card> cardIdForCurrency = new HashMap<>();

    Contact contactObject;
    String defaultCurrency;

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

    private CustomerAccount(Contact contactObject, List<Card> cards) {
        this.contactObject = contactObject;
        for (Card card : cards)
            cardIdForCurrency.put(card.getCurrency(), card);
    }

    public CustomerAccount addCurrency(String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return addCurrency(currency, 0);
    }
    public CustomerAccount addCurrency(String currency, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put(Constants.LightrailParameters.CONTACT_ID, getId());
        cardParams.put(Constants.LightrailParameters.CURRENCY, currency);
        cardParams.put(Constants.LightrailParameters.INITIAL_VALUE, initialValue);
        cardParams.put(Constants.LightrailParameters.CARD_TYPE, Constants.LightrailParameters.CARD_TYPE_ACCOUNT_CARD);
        return addCurrency(cardParams);
    }

    public CustomerAccount addCurrency(Map<String, Object> parameters) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CURRENCY
        ), parameters);

        String currency = (String) parameters.get(Constants.LightrailParameters.CURRENCY);
        String idempotencyKey = (String) parameters.get(Constants.LightrailParameters.USER_SUPPLIED_ID);

        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            parameters.put(Constants.LightrailParameters.USER_SUPPLIED_ID, idempotencyKey);
        }
        parameters.put(Constants.LightrailParameters.CARD_TYPE, Constants.LightrailParameters.CARD_TYPE_ACCOUNT_CARD);
        Card card = APICore.createCard(parameters);
        cardIdForCurrency.put(currency, card);
        return this;
    }

    public GiftCharge pendingCharge(int amount) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(defaultCurrency);
        return pendingCharge(amount, defaultCurrency);
    }
    public GiftCharge charge(int amount) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(defaultCurrency);
        return charge(amount, defaultCurrency);
    }

    public GiftCharge charge (int amount, boolean capture) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(defaultCurrency);
        return charge(amount, defaultCurrency, capture);
    }

    public GiftCharge pendingCharge(int amount, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return charge( amount,  currency, false);
    }

    public GiftCharge charge(int amount, String currency) throws InsufficientValueException, AuthorizationException, CouldNotFindObjectException, IOException {
        return charge( amount,  currency, true);
    }

    public GiftCharge charge(int amount, String currency, boolean capture) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put(Constants.LightrailParameters.AMOUNT, amount);
        chargeParams.put(Constants.LightrailParameters.CURRENCY, currency);
        chargeParams.put(Constants.LightrailParameters.CAPTURE, capture);
        return charge(chargeParams);
    }

    public GiftCharge charge(Map<String, Object> chargeParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), chargeParams);

        String currency = (String) chargeParams.get(Constants.LightrailParameters.CURRENCY);
        Card cardObject = cardIdForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account. Try adding this currency to the account first. ", currency));
        String cardId = cardObject.getCardId();

        chargeParams.put(Constants.LightrailParameters.CARD_ID, cardId);

        return GiftCharge.create(chargeParams);
    }

    public GiftFund fund(int amount) throws AuthorizationException, CouldNotFindObjectException, IOException {
        getCardFor(defaultCurrency);
        return fund( amount, defaultCurrency);
    }

    public GiftFund fund(int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put(Constants.LightrailParameters.AMOUNT, amount);
        chargeParams.put(Constants.LightrailParameters.CURRENCY, currency);
        return fund(chargeParams);
    }

    public GiftFund fund(Map<String, Object> fundParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), fundParams);

        String currency = (String) fundParams.get(Constants.LightrailParameters.CURRENCY);
        Card cardObject = getCardFor(currency);
        String cardId = cardObject.getCardId();
        fundParams.put(Constants.LightrailParameters.CARD_ID, cardId);

        return GiftFund.create(fundParams);
    }

    public GiftValue balance () throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        getCardFor(defaultCurrency);
        return balance(defaultCurrency);
    }

    public GiftValue balance (String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        return GiftValue.retrieveByCardId(getCardFor(currency).getCardId());
    }

    private Card getCardFor(String currency) {
        Card cardObject = cardIdForCurrency.get(currency);
        if (cardObject == null)
            throw new BadParameterException(String.format("Currency %s is not defined for this account. Try adding this currency to the account first. ", currency));
        else
            return cardObject;
    }

    public static CustomerAccount create(String email, String firstName, String lastName, String defaultCurrency, int initialBalance) throws AuthorizationException, CouldNotFindObjectException, IOException {
        return create(email, firstName, lastName).
                addCurrency(defaultCurrency,initialBalance).
                setDefaultCurrency(defaultCurrency);
    }
    public static CustomerAccount create(String email, String firstName, String lastName) throws AuthorizationException, CouldNotFindObjectException, IOException {
        if (email == null || email.isEmpty())
            throw new BadParameterException("Need to provide an email address for the new CustomerAccount.");

        Map<String, Object> customerAccountParams = new HashMap<>();
        customerAccountParams.put(Constants.LightrailParameters.EMAIL, email);
        if (firstName != null)
            customerAccountParams.put(Constants.LightrailParameters.FIRST_NAME, firstName);
        if (lastName != null)
            customerAccountParams.put(Constants.LightrailParameters.LAST_NAME, lastName);

        return create(customerAccountParams);
    }

    public static CustomerAccount create(Map<String, Object> customerAccountParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(Constants.LightrailParameters.EMAIL), customerAccountParams);

        String idempotencyKey = (String) customerAccountParams.get(Constants.LightrailParameters.USER_SUPPLIED_ID);

        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            customerAccountParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, idempotencyKey);
        }

        Contact contactObject = APICore.createContact(customerAccountParams);
        return new CustomerAccount(contactObject);
    }

    public static CustomerAccount retrieve(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.retrieveContact(customerAccountId);
        CardSearchResult cards = APICore.retrieveCardsOfContact(customerAccountId);
        CustomerAccount customerAccount = new CustomerAccount(contactObject, cards.getCards());
        if (cards.getCards().size() == 1)
            customerAccount.setDefaultCurrency(cards.getCards().get(0).getCurrency());
        return customerAccount;
    }

    public static void delete(String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        APICore.deleteContact(customerAccountId);
    }

    public CustomerAccount setDefaultCurrency(String defaultCurrency) {
        getCardFor(defaultCurrency);
        this.defaultCurrency = defaultCurrency;
        return this;
    }
}

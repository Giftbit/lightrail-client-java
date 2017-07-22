package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Card;
import com.lightrail.model.api.Contact;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class CustomerAccount {
    private Map<String, Card> cardIdForCurrency = new HashMap<>();

    Contact contactObject;

    public String getId () {
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

    public void addCurrency (String currency, int initialValue) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map <String, Object> cardParams = new HashMap<>();
        cardParams.put(Constants.LightrailParameters.CONTACT_ID, getId());
        cardParams.put(Constants.LightrailParameters.CURRENCY, currency);
        cardParams.put(Constants.LightrailParameters.INITIAL_VALUE, initialValue);
        cardParams.put(Constants.LightrailParameters.CARD_TYPE, Constants.LightrailParameters.CARD_TYPE_ACCOUNT_CARD);
        addCurrency(cardParams);

    }
    public void addCurrency (Map<String , Object> parameters) throws AuthorizationException, CouldNotFindObjectException, IOException {
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

    public static CustomerAccount retrieve (String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Contact contactObject = APICore.retrieveContact(customerAccountId);
        return new CustomerAccount(contactObject);
    }

    public static void  delete (String customerAccountId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        APICore.deleteContact(customerAccountId);
    }
}

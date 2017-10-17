package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.objects.RequestParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

public class LightrailContactTest {

    @Test
    public void walkThroughHappyPathTest() throws AuthorizationException, CouldNotFindObjectException, IOException, CurrencyMismatchException, InsufficientValueException {

        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        //create the account
        String email = "test@testy.ca";
        String firstName = "Test";
        String lastName = "McTestFace";

        LightrailContact customerAccount = LightrailContact.create(email, firstName, lastName);

        String contactId = customerAccount.getContactId();
        String userSuppliedId = customerAccount.getUserSuppliedId();

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assertEquals(0, customerAccount.getAvailableCurrencies().size());

        customerAccount = LightrailContact.retrieve(contactId);
        assertEquals(userSuppliedId, customerAccount.getUserSuppliedId());

        customerAccount = LightrailContact.retrieveByUserSuppliedId(userSuppliedId);
        assertEquals(contactId, customerAccount.getContactId());

        //retrieve the account
        String customerAccountId = customerAccount.getContactId();
        customerAccount = LightrailContact.retrieve(customerAccountId);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());

        int initialBalance = 500;
        String currency = "USD";

        //add a new currency
        customerAccount.addCurrency(currency, initialBalance);
        assert customerAccount.getAvailableCurrencies().contains(currency);

        String accountCardId = customerAccount.getCardFor(currency).getCardId();

        AccountCard accountCard = AccountCard.retrieveByCardId(accountCardId);
        assertEquals(LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD, accountCard.getCardType());
        assertEquals(customerAccountId, accountCard.getContactId());

        //retrieve again
        customerAccountId = customerAccount.getContactId();
        customerAccount = LightrailContact.retrieve(customerAccountId);
        assert customerAccount.getAvailableCurrencies().contains(currency);
        assertEquals(initialBalance, customerAccount.retrieveMaximumValue(currency));

        //charge simple
        int chargeValue = -400;
        customerAccount.createTransaction(chargeValue, currency);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue(currency));

        //fund simple
        int fundValue = 400;
        customerAccount.createTransaction(fundValue, currency);
        assertEquals(initialBalance + chargeValue + fundValue, customerAccount.retrieveMaximumValue(currency));

        //charge pending-void
        LightrailTransaction charge = customerAccount.createTransaction(chargeValue, currency, true);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue(currency));
        charge.doVoid();
        assertEquals(initialBalance, customerAccount.retrieveMaximumValue(currency));

        //charge pending-capture
        charge = customerAccount.createTransaction(chargeValue, currency, true);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue(currency));
        charge.capture();
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue(currency));
    }

    @Test
    public void walkThroughHappyPathTestWithDefaultCurrency() throws AuthorizationException, CouldNotFindObjectException, IOException, CurrencyMismatchException, InsufficientValueException {

        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        //create the account
        String email = "test@testy.ca";
        String firstName = "Test";
        String lastName = "McTestFace";
        String currency = "USD";
        int initialBalance = 500;


        LightrailContact customerAccount = LightrailContact.create(email, firstName, lastName, currency, initialBalance);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assertEquals(1, customerAccount.getAvailableCurrencies().size());


        //retrieve the account
        String customerAccountId = customerAccount.getContactId();
        customerAccount = LightrailContact.retrieve(customerAccountId);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assert customerAccount.getAvailableCurrencies().contains(currency);


        //retrieve again
        customerAccountId = customerAccount.getContactId();
        customerAccount = LightrailContact.retrieve(customerAccountId);
        assert customerAccount.getAvailableCurrencies().contains(currency);
        assertEquals(initialBalance, customerAccount.retrieveMaximumValue());

        //charge simple
        int chargeValue = -400;
        customerAccount.createTransaction(chargeValue);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue());

        //fund simple
        int fundValue = 400;
        customerAccount.createTransaction(fundValue);
        assertEquals(initialBalance + chargeValue + fundValue, customerAccount.retrieveMaximumValue());

        //charge pending-void
        LightrailTransaction charge = customerAccount.createPendingTransaction(chargeValue);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue());
        charge.doVoid();
        assertEquals(initialBalance, customerAccount.retrieveMaximumValue());

        //charge pending-capture
        charge = customerAccount.createTransaction(chargeValue, true);
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue());
        charge.capture();
        assertEquals(initialBalance + chargeValue, customerAccount.retrieveMaximumValue());

    }

    private LightrailContact createCustomerAccount(int initialBalance, String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        String email = "test@testy.ca";
        String firstName = "Test";
        String lastName = "McTestFace";

        LightrailContact customerAccount = LightrailContact.create(email, firstName, lastName, currency, initialBalance);
        return customerAccount;
    }


    @Test
    public void customerAccountChargeAndFund() throws AuthorizationException, CouldNotFindObjectException, IOException, CurrencyMismatchException, InsufficientValueException {
        int initialBalance = 500;
        String currency = "USD";

        LightrailContact contact = createCustomerAccount(initialBalance, currency);
        String contactId = contact.getContactId();
        String contactUserSuppliedId = contact.getUserSuppliedId();

        int amount = 100;

        RequestParameters params = new RequestParameters();
        params.put("contact", contactId);
        params.put("currency", currency);
        params.put("value", 0 - amount);

        LightrailTransaction.Create.create(params);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));

        params = new RequestParameters();
        params.put("contact", contactId);
        params.put("currency", currency);
        params.put("value", amount);
        LightrailTransaction fund = LightrailTransaction.Create.create(params);
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));

        //---------------------------------------------------------
        params = new RequestParameters();
        params.put("shopperId", contactUserSuppliedId);
        params.put("currency", currency);
        params.put("value", 0 - amount);

        LightrailTransaction simulatedTransaction = LightrailTransaction.Simulate.simulate(params);

        assertEquals(0-amount, simulatedTransaction.getValue().intValue());

        simulatedTransaction = LightrailTransaction.Simulate.byShopperId(contact.getShopperId(), 0-amount, currency);

        assertEquals(0-amount, simulatedTransaction.getValue().intValue());


        LightrailTransaction.Create.create(params);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));

        fund = LightrailTransaction.Create.byShopperId(contactUserSuppliedId, amount, currency);
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));
        //---------------------------------------------------------
        params = new RequestParameters();
        params.put("contact", contactId);
        params.put("currency", currency);
        params.put("value", 0 - amount);
        params.put("pending", true);

        LightrailTransaction charge = LightrailTransaction.Create.create(params);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));
        charge.doVoid();
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));

        params = new RequestParameters();
        params.put("contact", contactId);
        params.put("currency", currency);
        params.put("value", 0 - amount);
        params.put("pending", true);

        charge = LightrailTransaction.Create.create(params);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));
        charge.capture();
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));

        LightrailTransaction transaction = LightrailTransaction.Create.byContact(contactId, amount, currency);
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));
        assertEquals(contact.getCardFor(currency).getCardId(), transaction.getCardId());


        transaction = LightrailTransaction.Create.byContact(contactId, 0 - amount, currency);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));
        assertEquals(contact.getCardFor(currency).getCardId(), transaction.getCardId());


        transaction = LightrailTransaction.Create.byContact(contactId, amount, currency);
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));
        assertEquals(contact.getCardFor(currency).getCardId(), transaction.getCardId());

        charge = LightrailTransaction.Create.pendingByContact(contactId, 0 - amount, currency);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));
        charge.doVoid();
        assertEquals(initialBalance, contact.retrieveMaximumValue(currency));

        charge = LightrailTransaction.Create.pendingByContact(contactId, 0 - amount, currency);
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));
        charge.capture();
        assertEquals(initialBalance - amount, contact.retrieveMaximumValue(currency));

    }
}

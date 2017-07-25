package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

public class CustomerAccountTest {

    @Test
    public void walkThroughHappyPathTest() throws AuthorizationException, CouldNotFindObjectException, IOException, CurrencyMismatchException, InsufficientValueException {

        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        //create the account
        String email = "test@testy.ca";
        String firstName = "Test";
        String lastName = "McTestFace";

        CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assertEquals(0, customerAccount.getAvailableCurrencies().size());


        //retrieve the account
        String customerAccountId = customerAccount.getId();
        customerAccount = CustomerAccount.retrieve(customerAccountId);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());

        int initialBalance = 500;
        String currency = "USD";

        //add a new currency
        customerAccount.addCurrency(currency, initialBalance);
        assert customerAccount.getAvailableCurrencies().contains(currency);

        //retrieve again
        customerAccountId = customerAccount.getId();
        customerAccount = CustomerAccount.retrieve(customerAccountId);
        assert customerAccount.getAvailableCurrencies().contains(currency);
        assertEquals(initialBalance, customerAccount.balance(currency).getCurrentValue());

        //charge simple
        int chargeValue = 400;
        customerAccount.charge(chargeValue, currency);
        assertEquals(initialBalance - chargeValue, customerAccount.balance(currency).getCurrentValue());

        //fund simple
        int fundValue = 400;
        customerAccount.fund(fundValue, currency);
        assertEquals(initialBalance - chargeValue + fundValue, customerAccount.balance(currency).getCurrentValue());

        //charge pending-void
        GiftCharge charge = customerAccount.charge(chargeValue, currency, false);
        assertEquals(initialBalance - chargeValue, customerAccount.balance(currency).getCurrentValue());
        charge.cancel();
        assertEquals(initialBalance, customerAccount.balance(currency).getCurrentValue());

        //charge pending-capture
        charge = customerAccount.charge(chargeValue, currency, false);
        assertEquals(initialBalance - chargeValue, customerAccount.balance(currency).getCurrentValue());
        charge.capture();
        assertEquals(initialBalance - chargeValue, customerAccount.balance(currency).getCurrentValue());

//        CustomerAccount.delete(customerAccountId);
        try {
            customerAccount = CustomerAccount.retrieve(customerAccountId);
        } catch (Exception e) {
//            assertEquals(CouldNotFindObjectException.class.getName(), e.getClass().getName()); //todo: uncomment this after the API side is fixed
        }

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



        CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName, currency, initialBalance);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assertEquals(1, customerAccount.getAvailableCurrencies().size());


        //retrieve the account
        String customerAccountId = customerAccount.getId();
        customerAccount = CustomerAccount.retrieve(customerAccountId);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assert customerAccount.getAvailableCurrencies().contains(currency);


        //retrieve again
        customerAccountId = customerAccount.getId();
        customerAccount = CustomerAccount.retrieve(customerAccountId);
        assert customerAccount.getAvailableCurrencies().contains(currency);
        assertEquals(initialBalance, customerAccount.balance().getCurrentValue());

        //charge simple
        int chargeValue = 400;
        customerAccount.charge(chargeValue);
        assertEquals(initialBalance - chargeValue, customerAccount.balance().getCurrentValue());

        //fund simple
        int fundValue = 400;
        customerAccount.fund(fundValue);
        assertEquals(initialBalance - chargeValue + fundValue, customerAccount.balance().getCurrentValue());

        //charge pending-void
        GiftCharge charge = customerAccount.charge(chargeValue, false);
        assertEquals(initialBalance - chargeValue, customerAccount.balance().getCurrentValue());
        charge.cancel();
        assertEquals(initialBalance, customerAccount.balance().getCurrentValue());

        //charge pending-capture
        charge = customerAccount.charge(chargeValue, false);
        assertEquals(initialBalance - chargeValue, customerAccount.balance().getCurrentValue());
        charge.capture();
        assertEquals(initialBalance - chargeValue, customerAccount.balance().getCurrentValue());

//        CustomerAccount.delete(customerAccountId);
        try {
            customerAccount = CustomerAccount.retrieve(customerAccountId);
        } catch (Exception e) {
//            assertEquals(CouldNotFindObjectException.class.getName(), e.getClass().getName()); //todo: uncomment this after the API side is fixed
        }

    }
}

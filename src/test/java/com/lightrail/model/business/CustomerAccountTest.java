package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.Card;
import com.lightrail.net.APICore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

public class CustomerAccountTest {

    @Test
    public void createRetrieveAddCurrencyAndDeleteCustomerAccountTest() throws AuthorizationException, CouldNotFindObjectException, IOException {

        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");

        String email = "test@testy.ca";
        String firstName = "Test";
        String lastName = "McTestFace";

        CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());
        assertEquals(0, customerAccount.getAvailableCurrencies().size());

        String customerAccountId = customerAccount.getId();
        customerAccount = CustomerAccount.retrieve(customerAccountId);

        assertEquals(email, customerAccount.getEmail());
        assertEquals(firstName, customerAccount.getFirstName());
        assertEquals(lastName, customerAccount.getLastName());

        customerAccount.addCurrency("USD", 500);

        assert customerAccount.getAvailableCurrencies().contains("USD");

        CustomerAccount.delete(customerAccountId);
        try {
            customerAccount = CustomerAccount.retrieve(customerAccountId);

        } catch (Exception e) {
//            assertEquals(CouldNotFindObjectException.class.getName(), e.getClass().getName()); //todo: uncomment this after the API side is fixed
        }

    }
}

package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.helpers.LightrailConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContactHandler {
    public static Map<String, Object> handleContact(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> chargeParamsCopy = new HashMap<>(params);

        String contactId = (String) chargeParamsCopy.remove(LightrailConstants.Parameters.CONTACT);
        String requestedCurrency = (String) chargeParamsCopy.get(LightrailConstants.Parameters.CURRENCY);

        if (contactId != null) {
            if (requestedCurrency != null && !requestedCurrency.isEmpty()) {
                CustomerAccount account = CustomerAccount.retrieve(contactId);
                String cardId = account.getCardFor(requestedCurrency).getCardId();
                chargeParamsCopy.put(LightrailConstants.Parameters.CARD_ID, cardId);
            } else {
                throw new BadParameterException("Must provide a valid 'currency' when using 'contact'.");
            }
        }
        return chargeParamsCopy;
    }
}

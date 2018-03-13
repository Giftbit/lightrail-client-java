package com.lightrail;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lightrail.model.Card;
import com.lightrail.model.LightrailException;
import com.lightrail.model.Transaction;
import com.lightrail.params.CreateCardParams;
import com.lightrail.params.CreateTransactionParams;
import com.lightrail.utils.LightrailConstants;

import java.io.IOException;

import static java.lang.String.format;

public class Cards {
    public LightrailClient lr;

    public Cards(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateCardParams params) throws IOException, LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(lr.apiKey, com.lightrail.old.helpers.LightrailConstants.API.Endpoints.CREATE_CARD, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
        String card = lr.gson.fromJson(response, JsonObject.class).get("card").toString();
        return lr.gson.fromJson(card, Card.class);
    }

    public Card retrieveAccountCardByContactIdAndCurrency(String contactId, String currency) throws LightrailException, IOException {
        String response = lr.networkProvider.getAPIResponse(lr.apiKey, format("cards?cardType=ACCOUNT_CARD&contactId=%s&currency=%s", contactId, currency), LightrailConstants.API.REQUEST_METHOD_GET, null);

        if (response == null) {
            return null;
        }

        JsonArray jsonResponse = lr.gson.fromJson(response, JsonObject.class).getAsJsonArray("cards");
        if (jsonResponse.size() > 0) {
            String jsonCard = jsonResponse.get(0).toString();
            return lr.gson.fromJson(jsonCard, Card.class);
        } else {
            return null;
        }
    }


    public Transaction createTransaction(CreateTransactionParams params) throws IOException, LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(lr.apiKey, format("cards/%s/transactions", params.cardId), "POST", bodyJsonString);
        String transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction").toString();
        return lr.gson.fromJson(transaction, Transaction.class);
    }
}

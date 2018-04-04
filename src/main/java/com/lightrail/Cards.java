package com.lightrail;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.model.Card;
import com.lightrail.model.LightrailException;
import com.lightrail.model.Transaction;
import com.lightrail.params.CardSearchParams;
import com.lightrail.params.CompletePendingTransactionParams;
import com.lightrail.params.CreateCardParams;
import com.lightrail.params.CreateTransactionParams;

import java.util.ArrayList;

public class Cards {
    private LightrailClient lr;

    public Cards(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateCardParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.post(lr.endpointBuilder.createCard(), bodyJsonString);
        JsonElement card = lr.gson.fromJson(response, JsonObject.class).get("card");
        return lr.gson.fromJson(card, Card.class);
    }

    public ArrayList<Card> retrieve(CardSearchParams params) throws LightrailException {
        String urlQuery = lr.endpointBuilder.searchCardsByParams(params);

        String response = lr.networkProvider.get(urlQuery);

        JsonObject jsonResponse = lr.gson.fromJson(response, JsonObject.class);

        JsonArray cardResultsJsonArray = jsonResponse.getAsJsonArray("cards");
        if (cardResultsJsonArray.size() == 0) {
            return null;
        }

        ArrayList<Card> cardResults = new ArrayList<>();
        for (JsonElement jsonCard : cardResultsJsonArray) {
            Card card = lr.gson.fromJson(jsonCard, Card.class);
            cardResults.add(card);
        }

        return cardResults;
    }

    public Transaction createTransaction(CreateTransactionParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);

        String urlEndpoint;
        if (params.dryRun) {
            urlEndpoint = lr.endpointBuilder.dryRunTransaction(params.cardId);
        } else {
            urlEndpoint = lr.endpointBuilder.createTransaction(params.cardId);
        }

        String response = lr.networkProvider.post(urlEndpoint, bodyJsonString);
        JsonElement transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction");
        return lr.gson.fromJson(transaction, Transaction.class);
    }

    public Transaction retrieveTransaction(String cardId, String transactionId) throws LightrailException {
        String endpoint = lr.endpointBuilder.getTransaction(cardId, transactionId);
        String response = lr.networkProvider.get(endpoint);
        JsonElement transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction");
        return lr.gson.fromJson(transaction, Transaction.class);
    }

    public Transaction completePendingTransaction(CompletePendingTransactionParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot void or capture pending transaction with null params");
        }

        String endpoint;
        if (params.captureTransaction) {
            endpoint = lr.endpointBuilder.capturePendingTransaction(params.cardId, params.transactionId);
        } else {
            endpoint = lr.endpointBuilder.voidPendingTransaction(params.cardId, params.transactionId);
        }

        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.post(endpoint, bodyJsonString);
        JsonElement transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction");
        return lr.gson.fromJson(transaction, Transaction.class);
    }
}

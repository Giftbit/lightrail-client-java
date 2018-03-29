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
import com.lightrail.utils.LightrailConstants;

import java.util.ArrayList;

import static com.lightrail.network.EndpointBuilder.Transactions.CAPTURE;
import static com.lightrail.network.EndpointBuilder.Transactions.VOID;

public class Cards {
    private LightrailClient lr;

    public Cards(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateCardParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(
                lr.endpointBuilder.createCard(),
                LightrailConstants.API.REQUEST_METHOD_POST,
                bodyJsonString);
        JsonElement card = lr.gson.fromJson(response, JsonObject.class).get("card");
        return lr.gson.fromJson(card, Card.class);
    }

    public ArrayList<Card> retrieve(CardSearchParams params) throws LightrailException {
        String urlQuery = lr.endpointBuilder.searchCardsByParams(params);

        String response = lr.networkProvider.getAPIResponse(
                urlQuery,
                LightrailConstants.API.REQUEST_METHOD_GET,
                null);

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

        String urlEndpoint = lr.endpointBuilder.createTransaction(params.cardId);
        if (params.dryRun) {
            urlEndpoint = lr.endpointBuilder.dryRunTransaction(urlEndpoint);
        }

        String response = lr.networkProvider.getAPIResponse(
                urlEndpoint,
                LightrailConstants.API.REQUEST_METHOD_POST,
                bodyJsonString);
        JsonElement transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction");
        return lr.gson.fromJson(transaction, Transaction.class);
    }

    public Transaction completePendingTransaction(CompletePendingTransactionParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot void or capture pending transaction with null params");
        }
        String actionOnPending = params.captureTransaction ? CAPTURE.action : VOID.action;

        String endpoint = lr.endpointBuilder.completePendingTransaction(params.cardId, params.transactionId, actionOnPending);
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(
                endpoint,
                LightrailConstants.API.REQUEST_METHOD_POST,
                bodyJsonString);
        JsonElement transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction");
        return lr.gson.fromJson(transaction, Transaction.class);
    }
}

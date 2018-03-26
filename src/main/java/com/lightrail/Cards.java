package com.lightrail;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lightrail.model.Card;
import com.lightrail.model.LightrailException;
import com.lightrail.model.Transaction;
import com.lightrail.params.CardSearchParams;
import com.lightrail.params.CreateCardParams;
import com.lightrail.params.CreateTransactionParams;
import com.lightrail.params.HandlePendingTransactionParams;
import com.lightrail.utils.LightrailConstants;

import static java.lang.String.format;

public class Cards {
    private LightrailClient lr;

    public Cards(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateCardParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse("cards", LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
        String card = lr.gson.fromJson(response, JsonObject.class).get("card").toString();
        return lr.gson.fromJson(card, Card.class);
    }

    // todo: write method that returns all search results, not just one
    public Card retrieveSingleCardByParams(CardSearchParams params) throws LightrailException {
        String urlQuery = "cards?";
        if (params.cardType != null && !params.cardType.isEmpty()) {
            urlQuery = urlQuery + "cardType=" + lr.urlEncode(params.cardType) + "&";
        }
        if (params.userSuppliedId != null && !params.userSuppliedId.isEmpty()) {
            urlQuery = urlQuery + "userSuppliedId=" + lr.urlEncode(params.userSuppliedId) + "&";
        }
        if (params.contactId != null && !params.contactId.isEmpty()) {
            urlQuery = urlQuery + "contactId=" + lr.urlEncode(params.contactId) + "&";
        }
        if (params.currency != null && !params.currency.isEmpty()) {
            urlQuery = urlQuery + "currency=" + lr.urlEncode(params.currency) + "&";
        }

        String response = lr.networkProvider.getAPIResponse(urlQuery, "GET", null);

        JsonObject jsonResponse = lr.gson.fromJson(response, JsonObject.class);

        JsonArray cardResultsArray = jsonResponse.getAsJsonArray("cards");
        if (cardResultsArray.size() == 0) {
            return null;
        }

        String jsonCard = cardResultsArray.get(0).toString();
        return lr.gson.fromJson(jsonCard, Card.class);
    }

    public Transaction createTransaction(CreateTransactionParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);

        String urlEndpoint = format("cards/%s/transactions", lr.urlEncode(params.cardId));
        if (params.dryRun) {
            urlEndpoint = urlEndpoint + "/dryRun";
        }

        String response = lr.networkProvider.getAPIResponse(urlEndpoint, "POST", bodyJsonString);
        String transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction").toString();
        return lr.gson.fromJson(transaction, Transaction.class);
    }

    public Transaction handlePendingTransaction(HandlePendingTransactionParams params) throws LightrailException {
        if (!params.captureTransaction && !params.voidTransaction) {
            throw new LightrailException("Must set one of 'captureTransaction' or 'voidTransaction' to true");
        }
        if (params.captureTransaction && params.voidTransaction) {
            throw new LightrailException("Must set ONLY one of 'captureTransaction' or 'voidTransaction' to true");
        }
        String actionOnPending = params.captureTransaction ? "capture" : "void";

        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(format("cards/%s/transactions/%s/%s", lr.urlEncode(params.cardId), lr.urlEncode(params.transactionId), lr.urlEncode(actionOnPending)), "POST", bodyJsonString);
        String transaction = lr.gson.fromJson(response, JsonObject.class).get("transaction").toString();
        return lr.gson.fromJson(transaction, Transaction.class);
    }
}

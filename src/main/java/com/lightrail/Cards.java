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

public class Cards {
    private LightrailClient lr;

    public Cards(LightrailClient lr) {
        this.lr = lr;
    }

    public Card create(CreateCardParams params) throws LightrailException {
        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(
                LightrailConstants.API.Endpoints.CREATE_CARD,
                LightrailConstants.API.REQUEST_METHOD_POST,
                bodyJsonString);
        JsonElement card = lr.gson.fromJson(response, JsonObject.class).get("card");
        return lr.gson.fromJson(card, Card.class);
    }

    public ArrayList<Card> retrieve(CardSearchParams params) throws LightrailException {
        String urlQuery = LightrailConstants.API.Endpoints.SEARCH_CARDS;
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

        // todo possibility: LightrailConstants.API.EndpointsEnum.CARDS + "/" + id;

        String urlEndpoint = lr.endpointBuilder.createTransaction(params.cardId);
        if (params.dryRun) {
            urlEndpoint = urlEndpoint + LightrailConstants.API.Transactions.DRYRUN;
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
        String actionOnPending = params.captureTransaction ? LightrailConstants.API.Transactions.CAPTURE : LightrailConstants.API.Transactions.VOID;

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

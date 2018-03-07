package com.lightrail;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lightrail.model.Card;
import com.lightrail.model.LightrailException;
import com.lightrail.params.CreateAccountCardByContactIdParams;
import com.lightrail.utils.LightrailConstants;

import java.io.IOException;

import static java.lang.String.format;

public class Cards {
    public LightrailClient lr;
    public Gson gson;

    public Cards(LightrailClient lr) {
        this.lr = lr;
        this.gson = new Gson();
    }

    public Card create(CreateAccountCardByContactIdParams params) throws IOException, LightrailException {
        String bodyJsonString = gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(lr.apiKey, com.lightrail.old.helpers.LightrailConstants.API.Endpoints.CREATE_CARD, LightrailConstants.API.REQUEST_METHOD_GET, bodyJsonString);
        String card = gson.fromJson(response, JsonObject.class).get("card").toString();
        return gson.fromJson(card, Card.class);

    }

    public Card retrieveAccountCardByContactIdAndCurrency(String contactId, String currency) throws LightrailException, IOException {

        String response = lr.networkProvider.getAPIResponse(lr.apiKey, format("cards?cardType=ACCOUNT_CARD&contactId=%s&currency=%s", contactId, currency), LightrailConstants.API.REQUEST_METHOD_GET, null);

        JsonArray jsonResponse = gson.fromJson(response, JsonObject.class).getAsJsonArray("cards");

        if (jsonResponse.size() > 0) {
            String jsonCard = jsonResponse.get(0).toString();
            return gson.fromJson(jsonCard, Card.class);
        } else {
            throw new LightrailException(format("Could not find account card with contactId '%s' and currency '%s'", contactId, currency));
        }
    }


}

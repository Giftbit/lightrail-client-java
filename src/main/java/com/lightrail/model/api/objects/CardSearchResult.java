package com.lightrail.model.api.objects;

import com.google.gson.Gson;

import java.util.List;

public class CardSearchResult extends LightrailObject{
    public Card[] cards;
    public Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public Card[] getCards() {
        return cards;
    }

    public CardSearchResult (String jsonObject) {
        super(jsonObject);
        for (Card card: cards) {
            String cardJsonString = new Gson().toJson(card);
            Class<? extends LightrailObject> myClass = Card.class;
            JsonObjectRoot jsonRootAnnotation = myClass.getAnnotation(JsonObjectRoot.class);
            if (jsonRootAnnotation != null) {
                String jsonRootName = jsonRootAnnotation.value();
                cardJsonString = String.format("{\""+jsonRootName + "\":%s}", cardJsonString);
            }
            card.setRawJson(cardJsonString);
        }
    }
}


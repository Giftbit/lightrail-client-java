package com.lightrail.model.api.objects;

import com.google.gson.Gson;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;

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

    public Card getOneCard() throws CouldNotFindObjectException {
        if (cards.length == 1)
            return cards[0];
        else if (cards.length > 1)
            throw new BadParameterException("Search results include more than one Card.");
        else
            throw new CouldNotFindObjectException("Card does not exists.");
    }
}


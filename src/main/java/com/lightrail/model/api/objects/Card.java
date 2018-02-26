package com.lightrail.model.api.objects;


import com.google.gson.Gson;

import java.util.List;

@JsonObjectRoot("card")
public class Card extends LightrailObject {
    public String cardId;
    public String userSuppliedId;
    public String contactId;
    public String dateCreated;
    public String cardType;
    public String currency;
    //public String programId;
    public List<CardCategory> categories;

    public String getCardId() {
        return cardId;
    }

    public String getUserSuppliedId() {
        return userSuppliedId;
    }

    public String getContactId() {
        return contactId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCurrency() {
        return currency;
    }

    //public String getProgramId() { return programId; }

    public List<CardCategory> getCategories() {
        return categories;
    }

    public Card(String jsonObject) {
        new Gson().fromJson(jsonObject, Card.class);
    }

    public Card() {
    }

}

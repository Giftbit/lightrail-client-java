package com.lightrail.model.api;

import java.util.List;

@JsonObjectRoot("card")
public class Card {
    String cardId;
    String userSuppliedId;
    String contactId;
    String dateCreated;
    String cardType;
    String currency;
    List<CardCategory> categories;

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

    public List<CardCategory> getCategories() {
        return categories;
    }
}

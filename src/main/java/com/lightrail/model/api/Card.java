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
}

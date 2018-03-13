package com.lightrail.model;

import java.util.Objects;

public class Card {
    public String cardId;
    public String userSuppliedId;
    public String contactId;
    public String dateCreated;
    public String cardType;
    public String currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(cardId, card.cardId) &&
                Objects.equals(userSuppliedId, card.userSuppliedId) &&
                Objects.equals(contactId, card.contactId) &&
                Objects.equals(dateCreated, card.dateCreated) &&
                Objects.equals(cardType, card.cardType) &&
                Objects.equals(currency, card.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, userSuppliedId, contactId, dateCreated, cardType, currency);
    }
}

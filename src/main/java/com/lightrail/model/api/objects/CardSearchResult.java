package com.lightrail.model.api.objects;

import java.util.List;

public class CardSearchResult <T extends Card>{
    List<T> cards;

    public List<T> getCards() {
        return cards;
    }
}


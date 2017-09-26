package com.lightrail.model.api.objects;

import com.lightrail.helpers.LightrailConstants;

@JsonObjectRoot("details")
public class CardDetails extends LightrailObject {
    public String currency;
    public String cardType;
    public String cardId;
    public String asAtDate;
    public ValueStore[] valueStores;

    public String getCurrency() {
        return currency;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public String getAsAtDate() {
        return asAtDate;
    }

    public ValueStore[] getValueStores() {
        return valueStores;
    }

    public String getState() {
        for (ValueStore valueStore : valueStores) {
            if (LightrailConstants.API.ValueStores.TYPE_PRINCIPAL.equals(valueStore.getValueStoreType()))
                return valueStore.getState();
        }
        throw new RuntimeException("No PRINCIPAL ValueStore on Card");
    }

    public String getStartDate() {
        for (ValueStore valueStore : valueStores) {
            if (LightrailConstants.API.ValueStores.TYPE_PRINCIPAL.equals(valueStore.getValueStoreType()))
                return valueStore.getStartDate();
        }
        throw new RuntimeException("No PRINCIPAL ValueStore on Card");
    }

    public String getExpires() {
        for (ValueStore valueStore : valueStores) {
            if (LightrailConstants.API.ValueStores.TYPE_PRINCIPAL.equals(valueStore.getValueStoreType()))
                return valueStore.getExpires();
        }
        throw new RuntimeException("No PRINCIPAL ValueStore on Card");
    }


    public CardDetails(String jsonObject) {
        super(jsonObject);
    }
}

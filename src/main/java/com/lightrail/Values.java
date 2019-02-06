package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.Value;
import com.lightrail.params.values.CreateValueParams;

import java.io.IOException;

import static com.lightrail.network.NetworkUtils.urlEncode;

public class Values {

    private final LightrailClient lr;

    public Values(LightrailClient lr) {
        this.lr = lr;
    }

    public Value createValue(CreateValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/values", params, Value.class);
    }

    public Value getValue(String valueId) throws IOException, LightrailRestException {
        NullArgumentException.check(valueId, "valueId");

        return lr.networkProvider.get(String.format("/values/%s", urlEncode(valueId)), Value.class);
    }
}

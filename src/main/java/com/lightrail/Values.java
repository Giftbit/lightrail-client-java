package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.Value;
import com.lightrail.params.values.*;

import java.io.IOException;

import static com.lightrail.network.NetworkUtils.encodeUriComponent;
import static com.lightrail.network.NetworkUtils.toQueryString;

public class Values {

    private final LightrailClient lr;

    public Values(LightrailClient lr) {
        this.lr = lr;
    }

    public Value createValue(CreateValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/values", params, Value.class);
    }

    public Value createValue(CreateValueParams params, CreateValueQueryParams queryParams) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");
        NullArgumentException.check(params, "queryParams");

        return lr.networkProvider.post(String.format("/values%s", toQueryString(queryParams)), params, Value.class);
    }

    public Value getValue(String valueId) throws IOException, LightrailRestException {
        NullArgumentException.check(valueId, "valueId");

        return lr.networkProvider.get(String.format("/values/%s", encodeUriComponent(valueId)), Value.class);
    }

    public Value getValue(String valueId, GetValueQueryParams queryParams) throws IOException, LightrailRestException {
        NullArgumentException.check(valueId, "valueId");

        return lr.networkProvider.get(String.format("/values/%s%s", encodeUriComponent(valueId), toQueryString(queryParams)), Value.class);
    }

    public PaginatedList<Value> listValues() throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList("/values", Value.class);
    }

    public PaginatedList<Value> listValues(ListValuesParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/values%s", toQueryString(params)), Value.class);
    }

    public Value updateValue(String valueId, UpdateValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(valueId, "valueId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.patch(String.format("/values/%s", encodeUriComponent(valueId)), params, Value.class);
    }

    public Value updateValue(Value value, UpdateValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(value, "value");
        NullArgumentException.check(params, "params");

        return updateValue(value.id, params);
    }

    public Value changeValuesCode(String valueId, ChangeValuesCodeParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(valueId, "valueId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post(String.format("/values/%s/changeCode", encodeUriComponent(valueId)), params, Value.class);
    }

    public Value changeValuesCode(Value value, ChangeValuesCodeParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(value, "value");
        NullArgumentException.check(params, "params");

        return changeValuesCode(value.id, params);
    }
}

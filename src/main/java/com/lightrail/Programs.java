package com.lightrail;

import com.google.gson.JsonObject;
import com.lightrail.model.LightrailException;
import com.lightrail.model.Program;
import com.lightrail.params.CreateProgramParams;
import com.lightrail.utils.LightrailConstants;

public class Programs {
    private final LightrailClient lr;

    public Programs(LightrailClient lr) {
        this.lr = lr;
    }

    public Program create(CreateProgramParams params) throws LightrailException {
        if (params == null) {
            throw new LightrailException("Cannot create Program with params: null");
        }
        if (params.userSuppliedId == null) {
            throw new LightrailException("Missing parameter for program creation: userSuppliedId");
        }
        if (params.currency == null) {
            throw new LightrailException("Missing parameter for program creation: currency");
        }
        if (params.name == null) {
            throw new LightrailException("Missing parameter for program creation: name");
        }
        if (params.valueStoreType == null) {
            throw new LightrailException("Missing parameter for program creation: valueStoreType");
        }

        String bodyJsonString = lr.gson.toJson(params);
        String response = lr.networkProvider.getAPIResponse(
                LightrailConstants.API.Endpoints.CREATE_PROGRAM,
                LightrailConstants.API.REQUEST_METHOD_POST,
                bodyJsonString);
        String program = lr.gson.fromJson(response, JsonObject.class).get("program").toString();
        return lr.gson.fromJson(program, Program.class);
    }

    public Program retrieveById(String programId) throws LightrailException {
        if (programId == null) {
            throw new LightrailException("Cannot retrieve program for programId: null");
        }

        String response = lr.networkProvider.getAPIResponse(
                LightrailConstants.API.Endpoints.RETRIEVE_PROGRAM + lr.urlEncode(programId),
                LightrailConstants.API.REQUEST_METHOD_GET,
                null);
        String program = lr.gson.fromJson(response, JsonObject.class).get("program").toString();
        return lr.gson.fromJson(program, Program.class);
    }
}

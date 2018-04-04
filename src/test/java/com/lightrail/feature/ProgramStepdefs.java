package com.lightrail.feature;

import com.google.gson.*;
import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import com.lightrail.network.DefaultNetworkProvider;
import com.lightrail.params.CreateProgramParams;
import cucumber.api.java.en.Given;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static com.lightrail.feature.util.model.TestHelpers.*;
import static org.mockito.Mockito.mock;

public class ProgramStepdefs {
    private JsonObject jsonVariables = new JsonParser().parse(new FileReader("src/test/resources/programVariables.json")).getAsJsonObject();
    private DefaultNetworkProvider npMock = mock(DefaultNetworkProvider.class);
    private LightrailClient lr = new LightrailClient("123", "123", npMock);
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public ProgramStepdefs() throws FileNotFoundException, LightrailException {
    }

    @Given("^PROGRAM_CREATION a program is created with minimum parameters \\[(.[^\\]]+)\\] the following REST requests are made: \\[(.[^\\]]+)\\]$")
    public void programCreation(String params, String expectedRequestsAndResponses) throws IOException, LightrailException {
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, params);

        CreateProgramParams programParams = gson.fromJson(jsonParams.toString(), CreateProgramParams.class);
        lr.programs.create(programParams);

        verifyMock(reqResCollection, lr);
    }


    @Given("^PROGRAM_RETRIEVAL a program is retrieved by \\[(.[^\\]]+)\\] the following REST requests are made: \\[(.[^\\]]+)\\]$")
    public void programRetrieval(String params, String expectedRequestsAndResponses) throws IOException, LightrailException {
        Map<String, JsonElement> reqResCollection = getReqResCollection(jsonVariables, expectedRequestsAndResponses);
        setReqResExpectations(reqResCollection, lr);
        JsonObject jsonParams = getJsonParams(jsonVariables, params);

        if (jsonParams.get("programId") != null) {
            lr.programs.retrieveById(jsonParams.get("programId").getAsString());
        }

        verifyMock(reqResCollection, lr);
    }

}

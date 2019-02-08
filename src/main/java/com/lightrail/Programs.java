package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.Program;
import com.lightrail.params.programs.CreateProgramParams;
import com.lightrail.params.programs.ListProgramsParams;
import com.lightrail.params.programs.UpdateProgramParams;

import java.io.IOException;
import java.util.Map;

import static com.lightrail.network.NetworkUtils.encodeUriComponent;
import static com.lightrail.network.NetworkUtils.toQueryString;

public class Programs {

    private final LightrailClient lr;

    public Programs(LightrailClient lr) {
        this.lr = lr;
    }

    public Program createProgram(String programId) throws IOException, LightrailRestException {
        NullArgumentException.check(programId, "programId");

        return createProgram(new CreateProgramParams(programId));
    }

    public Program createProgram(CreateProgramParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/programs", params, Program.class);
    }

    public Program getProgram(String programId) throws IOException, LightrailRestException {
        NullArgumentException.check(programId, "programId");

        return lr.networkProvider.get(String.format("/programs/%s", encodeUriComponent(programId)), Program.class);
    }

    public PaginatedList<Program> listPrograms() throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList("/programs", Program.class);
    }

    public PaginatedList<Program> listPrograms(ListProgramsParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/programs%s", toQueryString(params)), Program.class);
    }

    public PaginatedList<Program> listPrograms(Map<String, String> params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/programs%s", toQueryString(params)), Program.class);
    }

    public Program updateProgram(String programId, UpdateProgramParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(programId, "programId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.patch(String.format("/programs/%s", encodeUriComponent(programId)), params, Program.class);
    }

    public Program updateProgram(Program program, UpdateProgramParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(program, "program");
        NullArgumentException.check(params, "params");

        return updateProgram(program.id, params);
    }
}

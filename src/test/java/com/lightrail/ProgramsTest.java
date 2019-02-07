package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.Currency;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.Program;
import com.lightrail.model.RedemptionRule;
import com.lightrail.params.programs.CreateProgramParams;
import com.lightrail.params.programs.ListProgramsParams;
import com.lightrail.params.programs.UpdateProgramParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static com.lightrail.TestUtils.*;
import static org.junit.Assert.*;

public class ProgramsTest {

    private LightrailClient lc;
    private Currency currency;

    @Before
    public void setUp() throws IOException, LightrailRestException {
        lc = getLightrailClient();
        currency = getOrCreateTestCurrency(lc);
    }

    @After
    public void tearDown() {
        lc = null;
    }

    @Test
    public void createMinimalProgram() throws Exception {
        CreateProgramParams params = new CreateProgramParams(generateId());
        params.name = "Espresso";
        params.currency = currency.code;

        Program programCreated = lc.programs.createProgram(params);

        assertEquals(params.id, programCreated.id);
        assertEquals(params.name, programCreated.name);
        assertEquals(params.currency, programCreated.currency);

        // check that all defaults set/not set
        assertFalse(programCreated.discount);
        assertFalse(programCreated.pretax);
        assertTrue(programCreated.active);
        assertNull(programCreated.discountSellerLiability);
        assertNull(programCreated.minInitialBalance);
        assertNull(programCreated.maxInitialBalance);
        assertNull(programCreated.fixedInitialBalances);
        assertNull(programCreated.fixedInitialUsesRemaining);
        assertNull(programCreated.redemptionRule);
        assertNull(programCreated.balanceRule);
        assertNull(programCreated.startDate);
        assertNull(programCreated.endDate);
        assertNull(programCreated.metadata);

        assertNotNull(programCreated.createdDate);
        assertNotNull(programCreated.updatedDate);
        assertNotNull(programCreated.createdBy);
    }

    @Test
    public void createGetAndListOneProgram() throws Exception {
        CreateProgramParams params = new CreateProgramParams(generateId());
        params.name = "Macchiato";
        params.currency = currency.code;
        params.discount = true;
        params.discountSellerLiability = 0.5;
        params.pretax = true;
        params.redemptionRule = new RedemptionRule("1 > 0", "Macchiato is better than nothing");
        params.minInitialBalance = 500;
        params.maxInitialBalance = 5000;
        params.fixedInitialUsesRemaining = new Integer[]{2};
        params.metadata = new HashMap<>();
        params.metadata.put("milk", "just a drop");
        params.metadata.put("foam", true);

        Program programCreated = lc.programs.createProgram(params);
        assertEquals(params.id, programCreated.id);
        assertEquals(params.name, programCreated.name);
        assertEquals(params.currency, programCreated.currency);
        assertEquals(params.discount, programCreated.discount);
        assertEquals(params.discountSellerLiability, programCreated.discountSellerLiability);
        assertEquals(params.pretax, programCreated.pretax);
        assertEquals(params.redemptionRule, programCreated.redemptionRule);
        assertEquals(params.minInitialBalance, programCreated.minInitialBalance);
        assertEquals(params.maxInitialBalance, programCreated.maxInitialBalance);
        assertEquals(params.fixedInitialUsesRemaining, programCreated.fixedInitialUsesRemaining);
        assertEquals(params.metadata, programCreated.metadata);
        assertNotNull(programCreated.createdDate);
        assertNotNull(programCreated.updatedDate);

        System.out.println(programCreated.active);
        System.out.println(programCreated.toString());

        assertTrue(programCreated.active);
        assertNull(programCreated.balanceRule);
        assertNull(programCreated.fixedInitialBalances);
        assertNull(programCreated.startDate);
        assertNull(programCreated.endDate);

        Program programGetted = lc.programs.getProgram(params.id);
        assertEquals(params.id, programGetted.id);
        assertEquals(params.name, programGetted.name);
        assertEquals(params.currency, programGetted.currency);
        assertEquals(params.discount, programGetted.discount);
        assertEquals(params.pretax, programGetted.pretax);
        assertEquals(params.minInitialBalance, programGetted.minInitialBalance);
        assertEquals(params.maxInitialBalance, programGetted.maxInitialBalance);
        assertEquals(params.fixedInitialUsesRemaining, programGetted.fixedInitialUsesRemaining);
        assertEquals(params.metadata, programGetted.metadata);
        assertNotNull(programGetted.createdDate);
        assertNotNull(programGetted.updatedDate);
        assertNotNull(programGetted.createdBy);

        ListProgramsParams listProgramsParams = new ListProgramsParams();
        listProgramsParams.id = params.id;
        PaginatedList<Program> programList = lc.programs.listPrograms(listProgramsParams);
        assertEquals(1, programList.size());
        assertEquals(params.id, programList.get(0).id);
    }

    @Test
    public void updateProgram() throws Exception {
        CreateProgramParams createParams = new CreateProgramParams(generateId());
        createParams.name = "Cappuccino";
        createParams.currency = currency.code;
        createParams.discount = true;
        createParams.pretax = true;
        createParams.active = true;
        createParams.fixedInitialBalances = new Integer[]{4444, 5555, 6666};
        createParams.fixedInitialUsesRemaining = new Integer[]{2};
        createParams.metadata = new HashMap<>();
        createParams.metadata.put("milk", true);

        Program programCreated = lc.programs.createProgram(createParams);
        assertEquals(createParams.id, programCreated.id);

        UpdateProgramParams updateParams = new UpdateProgramParams();
        updateParams.name = Optional.of("Affogato");
        updateParams.metadata = Optional.of(new HashMap<>());
        updateParams.metadata.get().put("iceCream", "vanilla");

        Program programUpdated = lc.programs.updateProgram(programCreated, updateParams);
        assertEquals(programCreated.id, programUpdated.id);
        assertEquals(updateParams.name.get(), programUpdated.name);
        assertEquals(updateParams.metadata.get(), programUpdated.metadata);
        assertEquals(programCreated.currency, programUpdated.currency);
        assertEquals(programCreated.discount, programUpdated.discount);
        assertEquals(programCreated.pretax, programUpdated.pretax);
        assertEquals(programCreated.active, programUpdated.active);
        assertEquals(programCreated.fixedInitialBalances, programUpdated.fixedInitialBalances);
        assertEquals(programCreated.fixedInitialUsesRemaining, programUpdated.fixedInitialUsesRemaining);
        assertNotEquals(programCreated, programUpdated);
    }

    @Test
    public void paginatePrograms() throws Exception {
        ListProgramsParams params = new ListProgramsParams();
        params.limit = 1;

        PaginatedList<Program> programsStart = lc.programs.listPrograms(params);
        assertEquals(1, programsStart.size());
        assertFalse(programsStart.hasFirst());
        assertFalse(programsStart.hasPrevious());
        assertTrue(programsStart.hasNext());
        assertTrue(programsStart.hasLast());

        PaginatedList<Program> programsNext = programsStart.getNext();
        assertEquals(1, programsNext.size());
        assertTrue(programsNext.hasFirst());
        assertTrue(programsNext.hasPrevious());
        assertTrue(programsNext.hasNext());
        assertTrue(programsNext.hasLast());

        PaginatedList<Program> programsPrev = programsNext.getPrevious();
        assertEquals(1, programsPrev.size());
        assertEquals(programsStart.get(0).id, programsPrev.get(0).id);
        assertTrue(programsPrev.hasNext());
        assertTrue(programsPrev.hasLast());

        PaginatedList<Program> programsFirst = programsNext.getFirst();
        assertEquals(1, programsFirst.size());
        assertEquals(programsStart.get(0).id, programsFirst.get(0).id);
        assertFalse(programsFirst.hasFirst());
        assertFalse(programsFirst.hasPrevious());
        assertTrue(programsFirst.hasNext());
        assertTrue(programsFirst.hasLast());

        PaginatedList<Program> programsLast = programsNext.getLast();
        assertEquals(1, programsLast.size());
        assertTrue(programsLast.hasFirst());
        assertTrue(programsLast.hasPrevious());
        assertFalse(programsLast.hasNext());
        assertFalse(programsLast.hasLast());
    }
}

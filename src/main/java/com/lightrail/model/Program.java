package com.lightrail.model;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Program {
    public String userSuppliedId;
    public String currency;
    public String name;
    public String valueStoreType;

    public int codeMinValue;
    public int codeMaxValue;
    public int[] fixedCodeValues;
    public Date programStartDate;
    public Date programExpiresDate;
    public String cardType;
    public HashMap<String, Object> metadata;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return codeMinValue == program.codeMinValue &&
                codeMaxValue == program.codeMaxValue &&
                Objects.equals(userSuppliedId, program.userSuppliedId) &&
                Objects.equals(currency, program.currency) &&
                Objects.equals(name, program.name) &&
                Objects.equals(valueStoreType, program.valueStoreType) &&
                Arrays.equals(fixedCodeValues, program.fixedCodeValues) &&
                Objects.equals(programStartDate, program.programStartDate) &&
                Objects.equals(programExpiresDate, program.programExpiresDate) &&
                Objects.equals(cardType, program.cardType) &&
                Objects.equals(metadata, program.metadata);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(userSuppliedId, currency, name, valueStoreType, codeMinValue, codeMaxValue, programStartDate, programExpiresDate, cardType, metadata);
        result = 31 * result + Arrays.hashCode(fixedCodeValues);
        return result;
    }
}

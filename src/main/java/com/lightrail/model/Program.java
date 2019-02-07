package com.lightrail.model;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Program {

    public String id;
    public String name;
    public String currency;
    public Boolean discount;
    public Boolean pretax;
    public Boolean active;
    public Double discountSellerLiability;
    public Integer minInitialBalance;
    public Integer maxInitialBalance;
    public Integer[] fixedInitialBalances;
    public Integer[] fixedInitialUsesRemaining;
    public RedemptionRule redemptionRule;
    public BalanceRule balanceRule;
    public Date startDate;
    public Date endDate;
    public Map<String, Object> metadata;
    public Date createdDate;
    public Date updatedDate;
    public String createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return discount == program.discount &&
                pretax == program.pretax &&
                active == program.active &&
                Objects.equals(id, program.id) &&
                Objects.equals(name, program.name) &&
                Objects.equals(currency, program.currency) &&
                Objects.equals(redemptionRule, program.redemptionRule) &&
                Objects.equals(balanceRule, program.balanceRule) &&
                Objects.equals(minInitialBalance, program.minInitialBalance) &&
                Objects.equals(maxInitialBalance, program.maxInitialBalance) &&
                Arrays.equals(fixedInitialBalances, program.fixedInitialBalances) &&
                Arrays.equals(fixedInitialUsesRemaining, program.fixedInitialUsesRemaining) &&
                Objects.equals(discountSellerLiability, program.discountSellerLiability) &&
                Objects.equals(startDate, program.startDate) &&
                Objects.equals(endDate, program.endDate) &&
                Objects.equals(metadata, program.metadata) &&
                Objects.equals(createdDate, program.createdDate) &&
                Objects.equals(updatedDate, program.updatedDate) &&
                Objects.equals(createdBy, program.createdBy);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, currency, discount, pretax, active, redemptionRule, balanceRule, minInitialBalance, maxInitialBalance, discountSellerLiability, startDate, endDate, metadata, createdDate, updatedDate, createdBy);
        result = 31 * result + Arrays.hashCode(fixedInitialBalances);
        result = 31 * result + Arrays.hashCode(fixedInitialUsesRemaining);
        return result;
    }
}

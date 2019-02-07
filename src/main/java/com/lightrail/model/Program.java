package com.lightrail.model;

import java.util.Date;
import java.util.List;
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
    public List<Integer> fixedInitialBalances;
    public List<Integer> fixedInitialUsesRemaining;
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
        return Objects.equals(id, program.id) &&
                Objects.equals(name, program.name) &&
                Objects.equals(currency, program.currency) &&
                Objects.equals(discount, program.discount) &&
                Objects.equals(pretax, program.pretax) &&
                Objects.equals(active, program.active) &&
                Objects.equals(discountSellerLiability, program.discountSellerLiability) &&
                Objects.equals(minInitialBalance, program.minInitialBalance) &&
                Objects.equals(maxInitialBalance, program.maxInitialBalance) &&
                Objects.equals(fixedInitialBalances, program.fixedInitialBalances) &&
                Objects.equals(fixedInitialUsesRemaining, program.fixedInitialUsesRemaining) &&
                Objects.equals(redemptionRule, program.redemptionRule) &&
                Objects.equals(balanceRule, program.balanceRule) &&
                Objects.equals(startDate, program.startDate) &&
                Objects.equals(endDate, program.endDate) &&
                Objects.equals(metadata, program.metadata) &&
                Objects.equals(createdDate, program.createdDate) &&
                Objects.equals(updatedDate, program.updatedDate) &&
                Objects.equals(createdBy, program.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, currency, discount, pretax, active, discountSellerLiability, minInitialBalance, maxInitialBalance, fixedInitialBalances, fixedInitialUsesRemaining, redemptionRule, balanceRule, startDate, endDate, metadata, createdDate, updatedDate, createdBy);
    }
}

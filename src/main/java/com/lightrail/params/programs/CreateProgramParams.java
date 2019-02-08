package com.lightrail.params.programs;

import com.lightrail.model.BalanceRule;
import com.lightrail.model.RedemptionRule;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CreateProgramParams {
    public String id;
    public String name;
    public String currency;
    public Boolean discount;
    public Boolean pretax;
    public Boolean active;
    public RedemptionRule redemptionRule;
    public BalanceRule balanceRule;
    public Integer minInitialBalance;
    public Integer maxInitialBalance;
    public List<Integer> fixedInitialBalances;
    public List<Integer> fixedInitialUsesRemaining;
    public Double discountSellerLiability;
    public Date startDate;
    public Date endDate;
    public Map<String, Object> metadata;

    public CreateProgramParams() {
    }

    public CreateProgramParams(String id) {
        this.id = id;
    }
}

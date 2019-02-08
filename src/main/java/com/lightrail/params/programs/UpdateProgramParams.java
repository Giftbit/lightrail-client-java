package com.lightrail.params.programs;

import com.lightrail.model.BalanceRule;
import com.lightrail.model.RedemptionRule;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Update a Program.  Leave fields as null to not change them,
 * set to Optional.empty() to set to null, set Optional.of(value)
 * to change.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class UpdateProgramParams {
    public Optional<String> name;
    public Optional<Boolean> active;
    public Optional<Boolean> discount;
    public Optional<Boolean> pretax;
    public Optional<RedemptionRule> redemptionRule;
    public Optional<BalanceRule> balanceRule;
    public Optional<Integer> minInitialBalance;
    public Optional<Integer> maxInitialBalance;
    public Optional<List<Integer>> fixedInitialBalances;
    public Optional<List<Integer>> fixedInitialUsesRemaining;
    public Optional<Double> discountSellerLiability;
    public Optional<Date> startDate;
    public Optional<Date> endDate;
    public Optional<Map<String, Object>> metadata;
}

package com.lightrail.params.values;

import com.google.gson.JsonElement;
import com.lightrail.model.BalanceRule;
import com.lightrail.model.RedemptionRule;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class UpdateValueParams {
    public Optional<Boolean> pretax;
    public Optional<Boolean> active;
    public Optional<Boolean> frozen;
    public Optional<Boolean> canceled;
    public Optional<RedemptionRule> redemptionRule;
    public Optional<BalanceRule> balanceRule;
    public Optional<Boolean> discount;
    public Optional<Double> discountSellerLiability;
    public Optional<Date> startDate;
    public Optional<Date> endDate;
    public Optional<Map<String, JsonElement>> metadata;
}

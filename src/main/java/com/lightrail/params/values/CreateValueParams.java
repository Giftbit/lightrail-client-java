package com.lightrail.params.values;

import com.google.gson.JsonObject;
import com.lightrail.model.BalanceRule;
import com.lightrail.model.RedemptionRule;

import java.util.Date;

public class CreateValueParams {
    public String id;
    public String currency;
    public Integer balance;
    public BalanceRule balanceRule;
    public Integer usesRemaining;
    public String programId;
    public String code;
    public Boolean isGenericCode;
    public String contactId;
    public Boolean pretax;
    public Boolean active;
    public RedemptionRule redemptionRule;
    public Boolean discount;
    public Float discountSellerLiability;
    public Date startDate;
    public Date endDate;
    public JsonObject metadata;

    public CreateValueParams() {
    }

    public CreateValueParams(String id) {
        this.id = id;
    }
}

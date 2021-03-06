package com.lightrail.params.values;

import com.lightrail.model.BalanceRule;
import com.lightrail.model.GenericCodeOptions;
import com.lightrail.model.RedemptionRule;

import java.util.Date;
import java.util.Map;

public class CreateValueParams {
    public String id;
    public String currency;
    public Integer balance;
    public BalanceRule balanceRule;
    public Integer usesRemaining;
    public String programId;
    public String code;
    public Boolean isGenericCode;
    public GenericCodeOptions genericCodeOptions;
    public String contactId;
    public Boolean pretax;
    public Boolean active;
    public RedemptionRule redemptionRule;
    public Boolean discount;
    public Double discountSellerLiability;
    public Date startDate;
    public Date endDate;
    public Map<String, Object> metadata;

    public CreateValueParams() {
    }

    public CreateValueParams(String id) {
        this.id = id;
    }
}

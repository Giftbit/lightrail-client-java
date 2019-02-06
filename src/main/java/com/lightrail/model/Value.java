package com.lightrail.model;

import com.google.gson.JsonObject;

import java.util.Date;

public class Value {
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
    public Boolean frozen;
    public Boolean canceled;
    public RedemptionRule redemptionRule;
    public Boolean discount;
    public Float discountSellerLiability;
    public Date startDate;
    public Date endDate;
    public JsonObject metadata;
    public Date createdDate;
    public Date updatedDate;
    public String createdBy;
}

package com.lightrail.model;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Value {

    public String id;
    public String currency;
    public Integer balance;
    public BalanceRule balanceRule;
    public Integer usesRemaining;
    public String programId;
    public String code;
    public Boolean isGenericCode;
    public String attachedFromValueId;
    public GenericCodeOptions genericCodeOptions;
    public String contactId;
    public Boolean pretax;
    public Boolean active;
    public Boolean frozen;
    public Boolean canceled;
    public RedemptionRule redemptionRule;
    public Boolean discount;
    public Double discountSellerLiability;
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
        Value value = (Value) o;
        return Objects.equals(id, value.id) &&
                Objects.equals(currency, value.currency) &&
                Objects.equals(balance, value.balance) &&
                Objects.equals(balanceRule, value.balanceRule) &&
                Objects.equals(usesRemaining, value.usesRemaining) &&
                Objects.equals(programId, value.programId) &&
                Objects.equals(code, value.code) &&
                Objects.equals(isGenericCode, value.isGenericCode) &&
                Objects.equals(attachedFromValueId, value.attachedFromValueId) &&
                Objects.equals(genericCodeOptions, value.genericCodeOptions) &&
                Objects.equals(contactId, value.contactId) &&
                Objects.equals(pretax, value.pretax) &&
                Objects.equals(active, value.active) &&
                Objects.equals(frozen, value.frozen) &&
                Objects.equals(canceled, value.canceled) &&
                Objects.equals(redemptionRule, value.redemptionRule) &&
                Objects.equals(discount, value.discount) &&
                Objects.equals(discountSellerLiability, value.discountSellerLiability) &&
                Objects.equals(startDate, value.startDate) &&
                Objects.equals(endDate, value.endDate) &&
                Objects.equals(metadata, value.metadata) &&
                Objects.equals(createdDate, value.createdDate) &&
                Objects.equals(updatedDate, value.updatedDate) &&
                Objects.equals(createdBy, value.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currency, balance, balanceRule, usesRemaining, programId, code, isGenericCode, contactId, pretax, active, frozen, canceled, redemptionRule, discount, discountSellerLiability, startDate, endDate, metadata, createdDate, updatedDate, createdBy);
    }
}

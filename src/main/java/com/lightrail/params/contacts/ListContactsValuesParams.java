package com.lightrail.params.contacts;

import com.lightrail.params.PaginatedParams;

import java.util.Date;

public class ListContactsValuesParams extends PaginatedParams {
    public String programId;
    public String currency;
    public Integer balance;
    public Integer usesRemaining;
    public Boolean discount;
    public Boolean active;
    public Boolean frozen;
    public Boolean canceled;
    public Boolean pretax;
    public Date startDate;
    public Date endDate;
    public Date createdDate;
    public Date updatedDate;
}

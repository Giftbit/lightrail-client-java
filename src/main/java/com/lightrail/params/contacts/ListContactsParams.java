package com.lightrail.params.contacts;

import com.lightrail.params.PaginatedParams;

public class ListContactsParams extends PaginatedParams {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String valueId;
    public Integer limit;
}

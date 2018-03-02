package com.lightrail.old.model.api.objects;

public class Pagination {
    public Integer count;
    public Integer limit;
    public Integer maxLimit;
    public Integer offset;
    public Integer totalCount;

    public Integer getCount() {
        return count;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getMaxLimit() {
        return maxLimit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getTotalCount() {
        return totalCount;
    }
}

package com.lightrail.model;

import java.util.Objects;

public class RedemptionRule {

    public String rule;
    public String explanation;

    public RedemptionRule() {
    }

    public RedemptionRule(String rule, String explanation) {
        this.rule = rule;
        this.explanation = explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedemptionRule that = (RedemptionRule) o;
        return Objects.equals(rule, that.rule) &&
                Objects.equals(explanation, that.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, explanation);
    }
}

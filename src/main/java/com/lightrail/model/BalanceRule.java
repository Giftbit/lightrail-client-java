package com.lightrail.model;

import java.util.Objects;

public class BalanceRule {

    public String rule;
    public String explanation;

    public BalanceRule() {
    }

    public BalanceRule(String rule, String explanation) {
        this.rule = rule;
        this.explanation = explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceRule that = (BalanceRule) o;
        return Objects.equals(rule, that.rule) &&
                Objects.equals(explanation, that.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, explanation);
    }
}

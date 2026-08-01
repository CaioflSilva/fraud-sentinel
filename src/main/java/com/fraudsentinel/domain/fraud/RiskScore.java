package com.fraudsentinel.domain.fraud;

public final class RiskScore {

    private final int value;

    private RiskScore(int value) {
        this.value = Math.min(100, Math.max(0, value));
    }

    public static RiskScore of(int value) {
        return new RiskScore(value);
    }

    public static RiskScore zero() {
        return new RiskScore(0);
    }

    public RiskScore add(int points) {
        return new RiskScore(this.value + points);
    }

    public int value() {
        return value;
    }

    public RiskLevel level() {
        return RiskLevel.fromScore(value);
    }

    @Override
    public String toString() {
        return value + " (" + level() + ")";
    }
}
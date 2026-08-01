package com.fraudsentinel.domain.fraud;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static RiskLevel fromScore(int score) {
        if (score <= 25) return LOW;
        if (score <= 50) return MEDIUM;
        if (score <= 75) return HIGH;
        return CRITICAL;
    }
}
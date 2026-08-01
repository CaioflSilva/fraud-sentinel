package com.fraudsentinel.domain.fraud.rules;

import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.transaction.Transaction;

import java.util.Set;

public class HighRiskLocationRule implements FraudRule {

    private static final Set<String> HIGH_RISK_LOCATIONS = Set.of(
            "UNKNOWN",
            "TOR",
            "VPN"
    );

    @Override
    public int evaluate(Transaction transaction) {
        var location = transaction.getLocation();

        if (location == null || location.isBlank()) {
            return 15;
        }

        var upperLocation = location.toUpperCase();
        for (var risky : HIGH_RISK_LOCATIONS) {
            if (upperLocation.contains(risky)) {
                return 30;
            }
        }
        return 0;
    }

    @Override
    public String name() {
        return "HIGH_RISK_LOCATION";
    }
}
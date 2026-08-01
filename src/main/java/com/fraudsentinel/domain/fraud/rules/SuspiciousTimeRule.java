package com.fraudsentinel.domain.fraud.rules;

import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.transaction.Transaction;

public class SuspiciousTimeRule implements FraudRule {

    private static final int NIGHT_START = 0;   // meia-noite
    private static final int NIGHT_END = 5;      // 5h da manha

    @Override
    public int evaluate(Transaction transaction) {
        var hour = transaction.getCreatedAt().getHour();

        if (hour >= NIGHT_START && hour < NIGHT_END) {
            return 15;
        }
        return 0;
    }

    @Override
    public String name() {
        return "SUSPICIOUS_TIME";
    }
}
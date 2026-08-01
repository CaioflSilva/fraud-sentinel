package com.fraudsentinel.domain.fraud.rules;

import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.transaction.Transaction;

import java.math.BigDecimal;

public class SuspiciousAmountRule implements FraudRule {

    private static final BigDecimal HIGH_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal CRITICAL_THRESHOLD = new BigDecimal("50000");

    @Override
    public int evaluate(Transaction transaction) {
        var amount = transaction.getMoney().getAmount();

        if (amount.compareTo(CRITICAL_THRESHOLD) >= 0) {
            return 40;
        }
        if (amount.compareTo(HIGH_THRESHOLD) >= 0) {
            return 25;
        }
        return 0;
    }

    @Override
    public String name() {
        return "SUSPICIOUS_AMOUNT";
    }
}
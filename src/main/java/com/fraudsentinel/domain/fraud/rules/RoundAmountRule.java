package com.fraudsentinel.domain.fraud.rules;

import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.transaction.Transaction;

import java.math.BigDecimal;

public class RoundAmountRule implements FraudRule {

    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("5000");

    @Override
    public int evaluate(Transaction transaction) {
        var amount = transaction.getMoney().getAmount();

        // Valores redondos e altos (multiplos de 1000 acima de 5000)
        if (amount.compareTo(MIN_AMOUNT) >= 0
                && amount.remainder(THOUSAND).compareTo(BigDecimal.ZERO) == 0) {
            return 10;
        }
        return 0;
    }

    @Override
    public String name() {
        return "ROUND_AMOUNT";
    }
}
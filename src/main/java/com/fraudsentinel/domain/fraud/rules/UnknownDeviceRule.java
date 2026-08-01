package com.fraudsentinel.domain.fraud.rules;

import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.transaction.Transaction;

public class UnknownDeviceRule implements FraudRule {

    @Override
    public int evaluate(Transaction transaction) {
        var deviceId = transaction.getDeviceId();

        if (deviceId == null || deviceId.isBlank()) {
            return 20;
        }
        return 0;
    }

    @Override
    public String name() {
        return "UNKNOWN_DEVICE";
    }
}
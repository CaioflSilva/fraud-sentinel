package com.fraudsentinel.domain.fraud;

import com.fraudsentinel.domain.transaction.Transaction;

public interface FraudRule {

    int evaluate(Transaction transaction);

    String name();
}
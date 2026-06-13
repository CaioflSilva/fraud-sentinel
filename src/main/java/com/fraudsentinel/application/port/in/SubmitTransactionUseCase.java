package com.fraudsentinel.application.port.in;

import com.fraudsentinel.domain.transaction.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubmitTransactionUseCase {

    Transaction execute(Command command);

    record Command(
            UUID userId,
            BigDecimal amount,
            String currency,
            String description,
            String originAccount,
            String targetAccount,
            String deviceId,
            String ipAddress,
            String location
    ) {}
}
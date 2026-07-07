package com.fraudsentinel.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID transactionId,
        UUID userId,
        BigDecimal amount,
        String currency,
        String originAccount,
        String targetAccount,
        String deviceId,
        String ipAddress,
        String location,
        LocalDateTime createdAt
) {}
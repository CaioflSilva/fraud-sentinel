package com.fraudsentinel.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID userId,
        BigDecimal amount,
        String currency,
        String status,
        String description,
        String originAccount,
        String targetAccount,
        LocalDateTime createdAt
) {}
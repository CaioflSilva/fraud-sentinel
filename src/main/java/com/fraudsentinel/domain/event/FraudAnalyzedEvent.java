package com.fraudsentinel.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FraudAnalyzedEvent(
        UUID transactionId,
        BigDecimal amount,
        String currency,
        String description,
        String deviceId,
        String location,
        int ruleScore,
        String riskLevel,
        Map<String, Integer> triggeredRules,
        List<String> reasons,
        LocalDateTime analyzedAt
) {}
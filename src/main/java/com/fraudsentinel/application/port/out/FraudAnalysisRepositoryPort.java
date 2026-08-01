package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.fraud.FraudAnalysis;

import java.util.UUID;

public interface FraudAnalysisRepositoryPort {

    void save(UUID transactionId, FraudAnalysis analysis, int finalScore);
}
package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.fraud.FraudAnalysis;

import java.util.UUID;

public interface FraudAnalysisRepositoryPort {

    void save(UUID transactionId, FraudAnalysis fraudAnalysis, int finalScore);

    void enrich(UUID transactionId, int aiScore, int finalScore, String aiJustification);
}
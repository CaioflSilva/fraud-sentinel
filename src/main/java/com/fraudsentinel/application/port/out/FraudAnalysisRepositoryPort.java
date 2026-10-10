package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.fraud.FraudAnalysis;
import com.fraudsentinel.domain.transaction.Transaction;

import java.util.UUID;

public interface FraudAnalysisRepositoryPort {

    void save(Transaction transaction, FraudAnalysis fraudAnalysis);

    void enrich(UUID transactionId, int aiScore, int finalScore, String aiJustification);
}
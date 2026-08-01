package com.fraudsentinel.configuration;

import com.fraudsentinel.application.port.in.AnalyzeTransactionUseCase;
import com.fraudsentinel.application.port.in.SubmitTransactionUseCase;
import com.fraudsentinel.application.port.out.FraudAnalysisRepositoryPort;
import com.fraudsentinel.application.port.out.OutboxPort;
import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.application.usecase.fraud.AnalyzeTransactionUseCaseImpl;
import com.fraudsentinel.application.usecase.transaction.SubmitTransactionUseCaseImpl;
import com.fraudsentinel.domain.fraud.FraudAnalysisEngine;
import com.fraudsentinel.domain.fraud.FraudRule;
import com.fraudsentinel.domain.fraud.rules.HighRiskLocationRule;
import com.fraudsentinel.domain.fraud.rules.RoundAmountRule;
import com.fraudsentinel.domain.fraud.rules.SuspiciousAmountRule;
import com.fraudsentinel.domain.fraud.rules.SuspiciousTimeRule;
import com.fraudsentinel.domain.fraud.rules.UnknownDeviceRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public SubmitTransactionUseCase submitTransactionUseCase(
            TransactionRepositoryPort repositoryPort,
            OutboxPort outboxPort) {
        return new SubmitTransactionUseCaseImpl(repositoryPort, outboxPort);
    }

    @Bean
    public FraudAnalysisEngine fraudAnalysisEngine() {
        List<FraudRule> rules = List.of(
                new SuspiciousAmountRule(),
                new SuspiciousTimeRule(),
                new UnknownDeviceRule(),
                new HighRiskLocationRule(),
                new RoundAmountRule()
        );
        return new FraudAnalysisEngine(rules);
    }

    @Bean
    public AnalyzeTransactionUseCase analyzeTransactionUseCase(
            TransactionRepositoryPort transactionRepositoryPort,
            FraudAnalysisRepositoryPort fraudAnalysisRepositoryPort,
            FraudAnalysisEngine fraudAnalysisEngine) {
        return new AnalyzeTransactionUseCaseImpl(
                transactionRepositoryPort,
                fraudAnalysisRepositoryPort,
                fraudAnalysisEngine
        );
    }
}
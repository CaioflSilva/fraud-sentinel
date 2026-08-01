package com.fraudsentinel.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudsentinel.application.port.out.FraudAnalysisRepositoryPort;
import com.fraudsentinel.domain.fraud.FraudAnalysis;
import com.fraudsentinel.infrastructure.persistence.entity.FraudAnalysisEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FraudAnalysisRepositoryAdapter implements FraudAnalysisRepositoryPort {

    private final FraudAnalysisJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(UUID transactionId, FraudAnalysis analysis, int finalScore) {
        try {
            var entity = new FraudAnalysisEntity();
            entity.setId(UUID.randomUUID());
            entity.setTransactionId(transactionId);
            entity.setRuleScore(analysis.ruleScore().value());
            entity.setAiScore(null);
            entity.setFinalScore(finalScore);
            entity.setRiskLevel(analysis.riskLevel().name());
            entity.setTriggeredRules(objectMapper.writeValueAsString(analysis.triggeredRules()));
            entity.setAiJustification(null);
            entity.setCreatedAt(LocalDateTime.now());

            jpaRepository.save(entity);

            log.info("Analise de fraude salva: transactionId={}, score={}, level={}",
                    transactionId, finalScore, analysis.riskLevel());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar analise de fraude", e);
        }
    }
}
package com.fraudsentinel.infrastructure.messaging;

import com.fraudsentinel.application.port.in.AnalyzeTransactionUseCase;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudAnalysisConsumer {

    private final AnalyzeTransactionUseCase analyzeTransactionUseCase;

    @KafkaListener(topics = "transaction-created", groupId = "fraud-sentinel-group")
    public void consume(TransactionCreatedEvent event) {
        log.info("Evento recebido: transactionId={}", event.transactionId());

        try {
            analyzeTransactionUseCase.execute(event.transactionId());
        } catch (Exception e) {
            log.error("Erro ao analisar transacao: transactionId={}", event.transactionId(), e);
            throw e; // Spring Kafka reenvia automaticamente
        }
    }
}
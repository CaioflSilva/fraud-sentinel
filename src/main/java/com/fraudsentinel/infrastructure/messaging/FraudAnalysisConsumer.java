package com.fraudsentinel.infrastructure.messaging;

import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.domain.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudAnalysisConsumer {

    private final TransactionRepositoryPort transactionRepositoryPort;

    @KafkaListener(topics = "transaction-created", groupId = "fraud-sentinel-group")
    public void consume(TransactionCreatedEvent event) {
        log.info("Evento recebido: transactionId={}", event.transactionId());

        var transactionOpt = transactionRepositoryPort.findById(event.transactionId());

        if (transactionOpt.isEmpty()) {
            log.warn("Transacao nao encontrada: transactionId={}", event.transactionId());
            return;
        }

        var transaction = transactionOpt.get();

        // Idempotencia: se ja nao e PENDING, ja foi processado
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transacao ja processada (idempotencia): transactionId={}, status={}",
                    event.transactionId(), transaction.getStatus());
            return;
        }

        try {
            transaction.advanceTo(TransactionStatus.ANALYZING);
            transactionRepositoryPort.save(transaction);
            log.info("Transacao em analise: transactionId={}", event.transactionId());
        } catch (Exception e) {
            log.error("Erro ao processar transacao: transactionId={}", event.transactionId(), e);
            throw e; // Spring Kafka reenvia automaticamente em caso de exception
        }
    }
}
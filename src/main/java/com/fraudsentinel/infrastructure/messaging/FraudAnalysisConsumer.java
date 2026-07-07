package com.fraudsentinel.infrastructure.messaging;

import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.domain.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudAnalysisConsumer {

    private final TransactionRepositoryPort transactionRepositoryPort;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.ALWAYS_RETRY_ON_ERROR
    )
    @KafkaListener(topics = "transaction-created", groupId = "fraud-sentinel-group")
    public void consume(TransactionCreatedEvent event) {
        log.info("Evento recebido: transactionId={}", event.transactionId());

        var transactionOpt = transactionRepositoryPort.findById(event.transactionId());

        if (transactionOpt.isEmpty()) {
            log.warn("Transacao nao encontrada: transactionId={}", event.transactionId());
            return;
        }

        var transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transacao ja processada (idempotencia): transactionId={}, status={}",
                    event.transactionId(), transaction.getStatus());
            return;
        }

        transaction.advanceTo(TransactionStatus.ANALYZING);
        transactionRepositoryPort.save(transaction);

        log.info("Transacao em analise: transactionId={}", event.transactionId());
    }

    @DltHandler
    public void handleDlt(TransactionCreatedEvent event) {
        log.error("Evento enviado para DLQ apos falhas: transactionId={}", event.transactionId());
    }
}
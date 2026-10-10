package com.fraudsentinel.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudsentinel.application.port.out.OutboxPort;
import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.infrastructure.persistence.entity.OutboxEventEntity;
import com.fraudsentinel.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxAdapter implements OutboxPort {

    private final OutboxEventJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveEvent(TransactionCreatedEvent event) {
        persist("Transaction", "TransactionCreated", event.transactionId(), event);
    }

    @Override
    public void saveEvent(FraudAnalyzedEvent event) {
        persist("FraudAnalysis", "FraudAnalyzed", event.transactionId(), event);
    }

    private void persist(String aggregateType, String eventType, UUID aggregateId, Object event) {
        try {
            var outboxEvent = new OutboxEventEntity();
            outboxEvent.setId(UUID.randomUUID());
            outboxEvent.setAggregateType(aggregateType);
            outboxEvent.setEventType(eventType);
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setPublished(false);
            outboxEvent.setCreatedAt(LocalDateTime.now());
            outboxRepository.save(outboxEvent);

            log.info("Outbox: evento {} gravado: aggregateId={}", eventType, aggregateId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gravar evento no outbox: " + eventType, e);
        }
    }
}
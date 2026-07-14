package com.fraudsentinel.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudsentinel.application.port.out.OutboxPort;
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
        try {
            var outboxEvent = new OutboxEventEntity();
            outboxEvent.setId(UUID.randomUUID());
            outboxEvent.setAggregateType("Transaction");
            outboxEvent.setEventType("TransactionCreated");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setPublished(false);
            outboxEvent.setCreatedAt(LocalDateTime.now());
            outboxRepository.save(outboxEvent);

            log.info("Outbox: evento gravado na mesma transacao: transactionId={}", event.transactionId());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gravar evento no outbox", e);
        }
    }
}
package com.fraudsentinel.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxEventJpaRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        var events = outboxRepository.findByPublishedFalseOrderByCreatedAtAsc();

        for (var event : events) {
            try {
                switch (event.getEventType()) {
                    case "TransactionCreated" -> {
                        var payload = objectMapper.readValue(event.getPayload(), TransactionCreatedEvent.class);
                        kafkaTemplate.send("transaction-created", payload.transactionId().toString(), payload)
                                .whenComplete((result, ex) -> {
                                    if (ex != null) {
                                        log.error("Outbox: falha ao publicar evento id={}", event.getId(), ex);
                                    }
                                });
                        log.info("Outbox: evento publicado topic=transaction-created, id={}", event.getId());
                    }
                    case "FraudAnalyzed" -> {
                        var payload = objectMapper.readValue(event.getPayload(), FraudAnalyzedEvent.class);
                        kafkaTemplate.send("fraud-analyzed", payload.transactionId().toString(), payload)
                                .whenComplete((result, ex) -> {
                                    if (ex != null) {
                                        log.error("Outbox: falha ao publicar evento id={}", event.getId(), ex);
                                    }
                                });
                        log.info("Outbox: evento publicado topic=fraud-analyzed, id={}", event.getId());
                    }
                    default -> {
                        log.warn("Outbox: eventType desconhecido: {}", event.getEventType());
                        continue;
                    }
                }

                event.setPublished(true);
                outboxRepository.save(event);

            } catch (Exception e) {
                log.error("Outbox: erro ao processar evento id={}", event.getId(), e);
            }
        }
    }
}
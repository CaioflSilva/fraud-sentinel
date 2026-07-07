package com.fraudsentinel.infrastructure.messaging;

import com.fraudsentinel.application.port.out.EventPublisherPort;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private static final String TOPIC = "transaction-created";

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    @Override
    public void publish(TransactionCreatedEvent event) {
        log.info("Publicando evento no Kafka: transactionId={}", event.transactionId());

        kafkaTemplate.send(TOPIC, event.transactionId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar evento: transactionId={}", event.transactionId(), ex);
                    } else {
                        log.info("Evento publicado com sucesso: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
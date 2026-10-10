package com.fraudsentinel.infrastructure.messaging;

import com.fraudsentinel.application.port.in.EnrichFraudAnalysisUseCase;
import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiEnrichmentConsumer {

    private final EnrichFraudAnalysisUseCase enrichFraudAnalysisUseCase;

    @KafkaListener(
            topics = "fraud-analyzed",
            groupId = "fraud-sentinel-ai-group",
            containerFactory = "fraudAnalyzedKafkaListenerContainerFactory"
    )
    public void consume(FraudAnalyzedEvent event) {
        log.info("Evento fraud-analyzed recebido: transactionId={}", event.transactionId());
        enrichFraudAnalysisUseCase.execute(event);
    }
}
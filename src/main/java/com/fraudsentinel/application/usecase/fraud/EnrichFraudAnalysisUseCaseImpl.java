package com.fraudsentinel.application.usecase.fraud;

import com.fraudsentinel.application.port.in.EnrichFraudAnalysisUseCase;
import com.fraudsentinel.application.port.out.AiAnalysisPort;
import com.fraudsentinel.application.port.out.FraudAnalysisRepositoryPort;
import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnrichFraudAnalysisUseCaseImpl implements EnrichFraudAnalysisUseCase {

    private static final double RULE_WEIGHT = 0.70;
    private static final double AI_WEIGHT = 0.30;

    private final AiAnalysisPort aiAnalysisPort;
    private final FraudAnalysisRepositoryPort fraudAnalysisRepositoryPort;

    @Override
    public void execute(FraudAnalyzedEvent event) {
        try {
            log.info("Iniciando enriquecimento IA: transactionId={}", event.transactionId());

            var enrichment = aiAnalysisPort.analyze(event);

            int finalScore = (int) Math.round(
                    (event.ruleScore() * RULE_WEIGHT) + (enrichment.score() * AI_WEIGHT)
            );

            fraudAnalysisRepositoryPort.enrich(
                    event.transactionId(),
                    enrichment.score(),
                    finalScore,
                    enrichment.justification()
            );

            log.info("Enriquecimento IA concluido: transactionId={}, aiScore={}, finalScore={}, justificativa={}",
                    event.transactionId(), enrichment.score(), finalScore, enrichment.justification());

        } catch (Exception e) {
            // Degradacao graciosa (ADR-0003): se a IA falhar, o antifraude continua funcionando
            log.error("Falha no enriquecimento IA (graceful degradation): transactionId={}",
                    event.transactionId(), e);
        }
    }
}
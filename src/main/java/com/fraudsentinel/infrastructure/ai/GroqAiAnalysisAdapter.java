package com.fraudsentinel.infrastructure.ai;

import com.fraudsentinel.application.port.out.AiAnalysisPort;
import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import com.fraudsentinel.domain.fraud.AiEnrichment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GroqAiAnalysisAdapter implements AiAnalysisPort {

    private final ChatClient chatClient;

    public GroqAiAnalysisAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AiEnrichment analyze(FraudAnalyzedEvent event) {
        var prompt = buildPrompt(event);

        log.info("Enviando analise para Groq/LLaMA: transactionId={}", event.transactionId());

        var response = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiResponse.class);

        log.info("Resposta da IA recebida: transactionId={}, score={}", event.transactionId(), response.score());

        return new AiEnrichment(response.score(), response.justification());
    }

    private String buildPrompt(FraudAnalyzedEvent event) {
        return """
                Voce e um analista antifraude especializado. Analise a transacao abaixo e retorne um JSON com dois campos:
                - "score": inteiro de 0 a 100 indicando probabilidade de fraude
                - "justification": explicacao curta em portugues (max 200 caracteres)
                
                Transacao:
                - ID: %s
                - Valor: %s %s
                - Descricao: %s
                - Dispositivo: %s
                - Localizacao: %s
                - Score do motor de regras: %d
                - Nivel de risco: %s
                - Regras disparadas: %s
                - Motivos: %s
                - Data da analise: %s
                
                Responda APENAS com o JSON, sem markdown, sem explicacao adicional.
                """.formatted(
                event.transactionId(),
                event.amount(),
                event.currency(),
                event.description(),
                event.deviceId(),
                event.location(),
                event.ruleScore(),
                event.riskLevel(),
                event.triggeredRules(),
                event.reasons(),
                event.analyzedAt()
        );
    }

    record AiResponse(int score, String justification) {}
}
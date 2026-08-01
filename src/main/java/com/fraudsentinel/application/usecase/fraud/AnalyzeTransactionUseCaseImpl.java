package com.fraudsentinel.application.usecase.fraud;

import com.fraudsentinel.application.port.in.AnalyzeTransactionUseCase;
import com.fraudsentinel.application.port.out.FraudAnalysisRepositoryPort;
import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.fraud.FraudAnalysisEngine;
import com.fraudsentinel.domain.transaction.TransactionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AnalyzeTransactionUseCaseImpl implements AnalyzeTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final FraudAnalysisRepositoryPort fraudAnalysisRepositoryPort;
    private final FraudAnalysisEngine fraudAnalysisEngine;

    @Override
    @Transactional
    public void execute(UUID transactionId) {
        var transactionOpt = transactionRepositoryPort.findById(transactionId);

        if (transactionOpt.isEmpty()) {
            log.warn("Transacao nao encontrada para analise: transactionId={}", transactionId);
            return;
        }

        var transaction = transactionOpt.get();

        // Idempotencia
        if (transaction.getStatus() != TransactionStatus.PENDING
                && transaction.getStatus() != TransactionStatus.ANALYZING) {
            log.info("Transacao ja analisada: transactionId={}, status={}",
                    transactionId, transaction.getStatus());
            return;
        }

        // Avanca para ANALYZING se ainda estiver PENDING
        if (transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.advanceTo(TransactionStatus.ANALYZING);
        }

        // Roda o motor de regras
        var analysis = fraudAnalysisEngine.analyze(transaction);
        var finalScore = analysis.ruleScore().value();

        log.info("Analise concluida: transactionId={}, score={}, level={}, regras={}",
                transactionId, finalScore, analysis.riskLevel(), analysis.reasons());

        // Persiste o resultado
        fraudAnalysisRepositoryPort.save(transactionId, analysis, finalScore);

        // Avanca para RULES_DONE
        transaction.advanceTo(TransactionStatus.RULES_DONE);

        // Decide: APPROVED ou FLAGGED (fallback sem IA - ADR-0003)
        if (analysis.shouldFlag()) {
            transaction.advanceTo(TransactionStatus.FLAGGED);
            log.warn("Transacao SINALIZADA como fraude: transactionId={}, score={}",
                    transactionId, finalScore);
        } else {
            transaction.advanceTo(TransactionStatus.APPROVED);
            log.info("Transacao APROVADA: transactionId={}, score={}", transactionId, finalScore);
        }

        transactionRepositoryPort.save(transaction);
    }
}
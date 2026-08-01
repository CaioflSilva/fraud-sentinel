package com.fraudsentinel.domain.fraud;

import com.fraudsentinel.domain.fraud.rules.HighRiskLocationRule;
import com.fraudsentinel.domain.fraud.rules.RoundAmountRule;
import com.fraudsentinel.domain.fraud.rules.SuspiciousAmountRule;
import com.fraudsentinel.domain.fraud.rules.SuspiciousTimeRule;
import com.fraudsentinel.domain.fraud.rules.UnknownDeviceRule;
import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FraudAnalysisEngineTest {

    private FraudAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        engine = new FraudAnalysisEngine(List.of(
                new SuspiciousAmountRule(),
                new SuspiciousTimeRule(),
                new UnknownDeviceRule(),
                new HighRiskLocationRule(),
                new RoundAmountRule()
        ));
    }

    @Test
    @DisplayName("Transacao normal deve ter score baixo e ser aprovada")
    void normalTransactionShouldBeLowRisk() {
        var tx = tx(new BigDecimal("247.83"), "device-conhecido", "Recife, PE");
        var analysis = engine.analyze(tx);

        assertEquals(0, analysis.ruleScore().value());
        assertEquals(RiskLevel.LOW, analysis.riskLevel());
        assertFalse(analysis.shouldFlag());
    }

    @Test
    @DisplayName("Transacao muito suspeita deve ser CRITICAL e sinalizada")
    void suspiciousTransactionShouldBeCritical() {
        var tx = tx(new BigDecimal("50000"), "", "VPN");
        var analysis = engine.analyze(tx);

        assertEquals(100, analysis.ruleScore().value());
        assertEquals(RiskLevel.CRITICAL, analysis.riskLevel());
        assertTrue(analysis.shouldFlag());
    }

    @Test
    @DisplayName("Deve registrar quais regras dispararam")
    void shouldTrackTriggeredRules() {
        var tx = tx(new BigDecimal("50000"), "", "VPN");
        var analysis = engine.analyze(tx);

        assertTrue(analysis.triggeredRules().containsKey("SUSPICIOUS_AMOUNT"));
        assertTrue(analysis.triggeredRules().containsKey("HIGH_RISK_LOCATION"));
        assertTrue(analysis.triggeredRules().containsKey("UNKNOWN_DEVICE"));
        assertFalse(analysis.reasons().isEmpty());
    }

    @Test
    @DisplayName("Valor 15000 dispara amount alto (25) + valor redondo (10) = 35, MEDIUM")
    void mediumHighTransaction() {
        var tx = tx(new BigDecimal("15000"), "device-conhecido", "Recife, PE");
        var analysis = engine.analyze(tx);

        // 25 (amount alto) + 10 (redondo) = 35 = MEDIUM
        assertEquals(35, analysis.ruleScore().value());
        assertEquals(RiskLevel.MEDIUM, analysis.riskLevel());
    }

    private Transaction tx(BigDecimal amount, String deviceId, String location) {
        return Transaction.create(
                UUID.randomUUID(),
                new Money(amount, "BRL"),
                "teste",
                "12345-6",
                "78901-2",
                deviceId,
                "189.28.100.50",
                location
        );
    }
}
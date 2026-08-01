package com.fraudsentinel.domain.fraud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskScoreTest {

    @Test
    @DisplayName("Deve criar score zero")
    void shouldCreateZeroScore() {
        var score = RiskScore.zero();
        assertEquals(0, score.value());
        assertEquals(RiskLevel.LOW, score.level());
    }

    @Test
    @DisplayName("Deve somar pontos")
    void shouldAddPoints() {
        var score = RiskScore.zero().add(30).add(20);
        assertEquals(50, score.value());
    }

    @Test
    @DisplayName("Deve limitar o score em 100")
    void shouldClampAt100() {
        var score = RiskScore.zero().add(60).add(60);
        assertEquals(100, score.value());
    }

    @Test
    @DisplayName("Deve limitar o score em 0 (nunca negativo)")
    void shouldClampAtZero() {
        var score = RiskScore.of(-50);
        assertEquals(0, score.value());
    }

    @Test
    @DisplayName("Deve classificar corretamente cada faixa")
    void shouldClassifyEachLevel() {
        assertEquals(RiskLevel.LOW, RiskScore.of(0).level());
        assertEquals(RiskLevel.LOW, RiskScore.of(25).level());
        assertEquals(RiskLevel.MEDIUM, RiskScore.of(26).level());
        assertEquals(RiskLevel.MEDIUM, RiskScore.of(50).level());
        assertEquals(RiskLevel.HIGH, RiskScore.of(51).level());
        assertEquals(RiskLevel.HIGH, RiskScore.of(75).level());
        assertEquals(RiskLevel.CRITICAL, RiskScore.of(76).level());
        assertEquals(RiskLevel.CRITICAL, RiskScore.of(100).level());
    }

    @Test
    @DisplayName("Imutabilidade: add nao altera o original")
    void shouldBeImmutable() {
        var original = RiskScore.of(10);
        original.add(20);
        assertEquals(10, original.value());
    }
}
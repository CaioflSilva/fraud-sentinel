package com.fraudsentinel.domain.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusTest {

    @Test
    @DisplayName("PENDING so pode ir para ANALYZING")
    void pendingShouldOnlyTransitionToAnalyzing() {
        assertTrue(TransactionStatus.PENDING.canTransitionTo(TransactionStatus.ANALYZING));
        assertFalse(TransactionStatus.PENDING.canTransitionTo(TransactionStatus.APPROVED));
        assertFalse(TransactionStatus.PENDING.canTransitionTo(TransactionStatus.FLAGGED));
    }

    @Test
    @DisplayName("ANALYZING so pode ir para RULES_DONE")
    void analyzingShouldOnlyTransitionToRulesDone() {
        assertTrue(TransactionStatus.ANALYZING.canTransitionTo(TransactionStatus.RULES_DONE));
        assertFalse(TransactionStatus.ANALYZING.canTransitionTo(TransactionStatus.APPROVED));
    }

    @Test
    @DisplayName("RULES_DONE so pode ir para ENRICHED")
    void rulesDoneShouldOnlyTransitionToEnriched() {
        assertTrue(TransactionStatus.RULES_DONE.canTransitionTo(TransactionStatus.ENRICHED));
        assertFalse(TransactionStatus.RULES_DONE.canTransitionTo(TransactionStatus.APPROVED));
    }

    @Test
    @DisplayName("ENRICHED pode ir para APPROVED ou FLAGGED")
    void enrichedShouldTransitionToApprovedOrFlagged() {
        assertTrue(TransactionStatus.ENRICHED.canTransitionTo(TransactionStatus.APPROVED));
        assertTrue(TransactionStatus.ENRICHED.canTransitionTo(TransactionStatus.FLAGGED));
        assertFalse(TransactionStatus.ENRICHED.canTransitionTo(TransactionStatus.PENDING));
    }

    @Test
    @DisplayName("APPROVED e FLAGGED sao estados finais")
    void terminalStatesShouldNotTransition() {
        for (TransactionStatus status : TransactionStatus.values()) {
            assertFalse(TransactionStatus.APPROVED.canTransitionTo(status));
            assertFalse(TransactionStatus.FLAGGED.canTransitionTo(status));
        }
    }
}
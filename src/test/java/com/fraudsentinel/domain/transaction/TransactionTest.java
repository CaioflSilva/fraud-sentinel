package com.fraudsentinel.domain.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Deve criar transacao com status PENDING")
    void shouldCreateTransactionWithPendingStatus() {
        var transaction = createSampleTransaction();

        assertNotNull(transaction.getId());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    @DisplayName("Deve avançar de PENDING para ANALYZING")
    void shouldAdvanceFromPendingToAnalyzing() {
        var transaction = createSampleTransaction();

        transaction.advanceTo(TransactionStatus.ANALYZING);

        assertEquals(TransactionStatus.ANALYZING, transaction.getStatus());
    }

    @Test
    @DisplayName("Deve percorrer todo o ciclo ate APPROVED")
    void shouldCompleteFullCycleToApproved() {
        var transaction = createSampleTransaction();

        transaction.advanceTo(TransactionStatus.ANALYZING);
        transaction.advanceTo(TransactionStatus.RULES_DONE);
        transaction.advanceTo(TransactionStatus.ENRICHED);
        transaction.advanceTo(TransactionStatus.APPROVED);

        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
    }

    @Test
    @DisplayName("Deve percorrer todo o ciclo ate FLAGGED")
    void shouldCompleteFullCycleToFlagged() {
        var transaction = createSampleTransaction();

        transaction.advanceTo(TransactionStatus.ANALYZING);
        transaction.advanceTo(TransactionStatus.RULES_DONE);
        transaction.advanceTo(TransactionStatus.ENRICHED);
        transaction.advanceTo(TransactionStatus.FLAGGED);

        assertEquals(TransactionStatus.FLAGGED, transaction.getStatus());
    }

    @Test
    @DisplayName("Deve rejeitar transicao invalida")
    void shouldRejectInvalidTransition() {
        var transaction = createSampleTransaction();

        assertThrows(IllegalStateException.class,
                () -> transaction.advanceTo(TransactionStatus.APPROVED));
    }

    @Test
    @DisplayName("Deve preservar dados do Money")
    void shouldPreserveMoneyData() {
        var transaction = createSampleTransaction();

        assertEquals(new BigDecimal("1500.00"), transaction.getMoney().getAmount());
        assertEquals("BRL", transaction.getMoney().getCurrency());
    }

    private Transaction createSampleTransaction() {
        return Transaction.create(
                UUID.randomUUID(),
                new Money(new BigDecimal("1500.00"), "BRL"),
                "Transferencia PIX",
                "12345-6",
                "78901-2",
                "device-abc-123",
                "189.28.100.50",
                "Recife, PE"
        );
    }
}
package com.fraudsentinel.domain.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Deve criar Money com valores validos")
    void shouldCreateMoneyWithValidValues() {
        var money = new Money(new BigDecimal("100.50"), "BRL");

        assertEquals(new BigDecimal("100.50"), money.getAmount());
        assertEquals("BRL", money.getCurrency());
    }

    @Test
    @DisplayName("Deve rejeitar valor zero")
    void shouldRejectZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.ZERO, "BRL"));
    }

    @Test
    @DisplayName("Deve rejeitar valor negativo")
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-10"), "BRL"));
    }

    @Test
    @DisplayName("Deve rejeitar moeda com formato invalido")
    void shouldRejectInvalidCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("100"), "real"));
    }

    @Test
    @DisplayName("Deve rejeitar moeda nula")
    void shouldRejectNullCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("100"), null));
    }

    @Test
    @DisplayName("Deve considerar iguais Money com mesmo valor e moeda")
    void shouldBeEqualWithSameAmountAndCurrency() {
        var money1 = new Money(new BigDecimal("100.00"), "BRL");
        var money2 = new Money(new BigDecimal("100.0"), "BRL");

        assertEquals(money1, money2);
    }

    @Test
    @DisplayName("Deve considerar diferentes Money com valores distintos")
    void shouldNotBeEqualWithDifferentAmount() {
        var money1 = new Money(new BigDecimal("100"), "BRL");
        var money2 = new Money(new BigDecimal("200"), "BRL");

        assertNotEquals(money1, money2);
    }
}
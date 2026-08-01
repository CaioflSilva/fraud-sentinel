package com.fraudsentinel.domain.fraud;

import com.fraudsentinel.domain.fraud.rules.HighRiskLocationRule;
import com.fraudsentinel.domain.fraud.rules.RoundAmountRule;
import com.fraudsentinel.domain.fraud.rules.SuspiciousAmountRule;
import com.fraudsentinel.domain.fraud.rules.UnknownDeviceRule;
import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FraudRulesTest {

    @Test
    @DisplayName("SuspiciousAmount: valor critico soma 40")
    void suspiciousAmountCritical() {
        var rule = new SuspiciousAmountRule();
        var tx = tx(new BigDecimal("50000"), "device-1", "Recife");
        assertEquals(40, rule.evaluate(tx));
    }

    @Test
    @DisplayName("SuspiciousAmount: valor alto soma 25")
    void suspiciousAmountHigh() {
        var rule = new SuspiciousAmountRule();
        var tx = tx(new BigDecimal("15000"), "device-1", "Recife");
        assertEquals(25, rule.evaluate(tx));
    }

    @Test
    @DisplayName("SuspiciousAmount: valor baixo soma 0")
    void suspiciousAmountLow() {
        var rule = new SuspiciousAmountRule();
        var tx = tx(new BigDecimal("500"), "device-1", "Recife");
        assertEquals(0, rule.evaluate(tx));
    }

    @Test
    @DisplayName("UnknownDevice: sem device soma 20")
    void unknownDevice() {
        var rule = new UnknownDeviceRule();
        var tx = tx(new BigDecimal("500"), "", "Recife");
        assertEquals(20, rule.evaluate(tx));
    }

    @Test
    @DisplayName("UnknownDevice: com device soma 0")
    void knownDevice() {
        var rule = new UnknownDeviceRule();
        var tx = tx(new BigDecimal("500"), "device-1", "Recife");
        assertEquals(0, rule.evaluate(tx));
    }

    @Test
    @DisplayName("HighRiskLocation: VPN soma 30")
    void highRiskLocationVpn() {
        var rule = new HighRiskLocationRule();
        var tx = tx(new BigDecimal("500"), "device-1", "VPN");
        assertEquals(30, rule.evaluate(tx));
    }

    @Test
    @DisplayName("HighRiskLocation: local normal soma 0")
    void normalLocation() {
        var rule = new HighRiskLocationRule();
        var tx = tx(new BigDecimal("500"), "device-1", "Recife, PE");
        assertEquals(0, rule.evaluate(tx));
    }

    @Test
    @DisplayName("RoundAmount: valor redondo alto soma 10")
    void roundAmount() {
        var rule = new RoundAmountRule();
        var tx = tx(new BigDecimal("10000"), "device-1", "Recife");
        assertEquals(10, rule.evaluate(tx));
    }

    @Test
    @DisplayName("RoundAmount: valor quebrado soma 0")
    void nonRoundAmount() {
        var rule = new RoundAmountRule();
        var tx = tx(new BigDecimal("247.83"), "device-1", "Recife");
        assertEquals(0, rule.evaluate(tx));
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
package com.fraudsentinel.infrastructure.persistence;

import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import com.fraudsentinel.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class TransactionRepositoryAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TransactionRepositoryPort repositoryPort;

    @Test
    @DisplayName("Deve salvar e recuperar transacao do banco")
    void shouldSaveAndRetrieveTransaction() {
        var transaction = Transaction.create(
                UUID.randomUUID(),
                new Money(new BigDecimal("2500.00"), "BRL"),
                "Pagamento fornecedor",
                "12345-6",
                "78901-2",
                "device-xyz",
                "189.28.100.50",
                "Recife, PE"
        );

        var saved = repositoryPort.save(transaction);

        assertNotNull(saved.getId());
        assertEquals(TransactionStatus.PENDING, saved.getStatus());

        var found = repositoryPort.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("BRL", found.get().getMoney().getCurrency());
        assertEquals(new BigDecimal("2500.0000"), found.get().getMoney().getAmount());
        assertEquals("Recife, PE", found.get().getLocation());
    }

    @Test
    @DisplayName("Deve retornar vazio para ID inexistente")
    void shouldReturnEmptyForNonExistentId() {
        var found = repositoryPort.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }
}
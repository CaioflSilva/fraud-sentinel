package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.transaction.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
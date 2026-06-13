package com.fraudsentinel.infrastructure.persistence.repository;

import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.transaction.Transaction;
import com.fraudsentinel.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = mapper.toEntity(transaction);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}
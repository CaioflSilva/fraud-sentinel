package com.fraudsentinel.infrastructure.persistence.repository;

import com.fraudsentinel.infrastructure.persistence.entity.FraudAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FraudAnalysisJpaRepository extends JpaRepository<FraudAnalysisEntity, UUID> {

    Optional<FraudAnalysisEntity> findByTransactionId(UUID transactionId);
}
package com.fraudsentinel.infrastructure.persistence.mapper;

import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import com.fraudsentinel.domain.transaction.TransactionStatus;
import com.fraudsentinel.infrastructure.persistence.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionPersistenceMapper {

    @Mapping(target = "amount", expression = "java(transaction.getMoney().getAmount())")
    @Mapping(target = "currency", expression = "java(transaction.getMoney().getCurrency())")
    @Mapping(target = "status", expression = "java(transaction.getStatus().name())")
    TransactionEntity toEntity(Transaction transaction);

    default Transaction toDomain(TransactionEntity entity) {
        return Transaction.reconstitute(
                entity.getId(),
                entity.getUserId(),
                new Money(entity.getAmount(), entity.getCurrency()),
                TransactionStatus.valueOf(entity.getStatus()),
                entity.getDescription(),
                entity.getOriginAccount(),
                entity.getTargetAccount(),
                entity.getDeviceId(),
                entity.getIpAddress(),
                entity.getLocation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
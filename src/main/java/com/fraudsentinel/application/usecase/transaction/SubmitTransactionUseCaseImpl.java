package com.fraudsentinel.application.usecase.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudsentinel.application.port.in.SubmitTransactionUseCase;
import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import com.fraudsentinel.infrastructure.persistence.entity.OutboxEventEntity;
import com.fraudsentinel.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitTransactionUseCaseImpl implements SubmitTransactionUseCase {

    private final TransactionRepositoryPort repositoryPort;
    private final OutboxEventJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Transaction execute(Command command) {
        var money = new Money(command.amount(), command.currency());

        var transaction = Transaction.create(
                command.userId(),
                money,
                command.description(),
                command.originAccount(),
                command.targetAccount(),
                command.deviceId(),
                command.ipAddress(),
                command.location()
        );

        var saved = repositoryPort.save(transaction);

        var event = new TransactionCreatedEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getMoney().getAmount(),
                saved.getMoney().getCurrency(),
                saved.getOriginAccount(),
                saved.getTargetAccount(),
                saved.getDeviceId(),
                saved.getIpAddress(),
                saved.getLocation(),
                saved.getCreatedAt()
        );

        try {
            var outboxEvent = new OutboxEventEntity();
            outboxEvent.setId(UUID.randomUUID());
            outboxEvent.setAggregateType("Transaction");
            outboxEvent.setEventType("TransactionCreated");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setPublished(false);
            outboxEvent.setCreatedAt(LocalDateTime.now());
            outboxRepository.save(outboxEvent);

            log.info("Outbox: evento gravado na mesma transacao: transactionId={}", saved.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gravar evento no outbox", e);
        }

        return saved;
    }
}
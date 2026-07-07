package com.fraudsentinel.application.usecase.transaction;

import com.fraudsentinel.application.port.in.SubmitTransactionUseCase;
import com.fraudsentinel.application.port.out.EventPublisherPort;
import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.domain.event.TransactionCreatedEvent;
import com.fraudsentinel.domain.transaction.Money;
import com.fraudsentinel.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitTransactionUseCaseImpl implements SubmitTransactionUseCase {

    private final TransactionRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisherPort;

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

        eventPublisherPort.publish(event);

        return saved;
    }
}
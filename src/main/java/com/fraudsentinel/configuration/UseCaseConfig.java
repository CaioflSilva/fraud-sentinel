package com.fraudsentinel.configuration;

import com.fraudsentinel.application.port.in.SubmitTransactionUseCase;
import com.fraudsentinel.application.port.out.OutboxPort;
import com.fraudsentinel.application.port.out.TransactionRepositoryPort;
import com.fraudsentinel.application.usecase.transaction.SubmitTransactionUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public SubmitTransactionUseCase submitTransactionUseCase(
            TransactionRepositoryPort repositoryPort,
            OutboxPort outboxPort) {
        return new SubmitTransactionUseCaseImpl(repositoryPort, outboxPort);
    }
}
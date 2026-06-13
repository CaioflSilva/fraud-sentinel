package com.fraudsentinel.application.port.in;

import java.util.UUID;

public interface AnalyzeTransactionUseCase {

    void execute(UUID transactionId);
}
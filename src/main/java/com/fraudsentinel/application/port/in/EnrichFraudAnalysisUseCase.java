package com.fraudsentinel.application.port.in;

import com.fraudsentinel.domain.event.FraudAnalyzedEvent;

public interface EnrichFraudAnalysisUseCase {

    void execute(FraudAnalyzedEvent event);
}
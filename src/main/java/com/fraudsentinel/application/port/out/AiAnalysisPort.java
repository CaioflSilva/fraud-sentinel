package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.event.FraudAnalyzedEvent;
import com.fraudsentinel.domain.fraud.AiEnrichment;

public interface AiAnalysisPort {

    AiEnrichment analyze(FraudAnalyzedEvent event);
}
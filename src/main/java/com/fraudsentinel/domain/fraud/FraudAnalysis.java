package com.fraudsentinel.domain.fraud;

import java.util.List;
import java.util.Map;

public record FraudAnalysis(
        RiskScore ruleScore,
        Map<String, Integer> triggeredRules,
        List<String> reasons
) {

    public RiskLevel riskLevel() {
        return ruleScore.level();
    }

    public boolean shouldFlag() {
        var level = riskLevel();
        return level == RiskLevel.HIGH || level == RiskLevel.CRITICAL;
    }
}
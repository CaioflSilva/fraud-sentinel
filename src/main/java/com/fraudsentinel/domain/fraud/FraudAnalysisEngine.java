package com.fraudsentinel.domain.fraud;

import com.fraudsentinel.domain.transaction.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FraudAnalysisEngine {

    private final List<FraudRule> rules;

    public FraudAnalysisEngine(List<FraudRule> rules) {
        this.rules = rules;
    }

    public FraudAnalysis analyze(Transaction transaction) {
        var score = RiskScore.zero();
        var triggeredRules = new LinkedHashMap<String, Integer>();
        var reasons = new ArrayList<String>();

        for (var rule : rules) {
            var points = rule.evaluate(transaction);
            if (points > 0) {
                score = score.add(points);
                triggeredRules.put(rule.name(), points);
                reasons.add(rule.name() + " (+" + points + ")");
            }
        }

        return new FraudAnalysis(score, triggeredRules, reasons);
    }
}
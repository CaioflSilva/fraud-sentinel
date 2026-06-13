package com.fraudsentinel.domain.transaction;

public enum TransactionStatus {

    PENDING,
    ANALYZING,
    RULES_DONE,
    ENRICHED,
    APPROVED,
    FLAGGED;

    public boolean canTransitionTo(TransactionStatus next) {
        return switch (this) {
            case PENDING -> next == ANALYZING;
            case ANALYZING -> next == RULES_DONE;
            case RULES_DONE -> next == ENRICHED;
            case ENRICHED -> next == APPROVED || next == FLAGGED;
            case APPROVED, FLAGGED -> false;
        };
    }
}
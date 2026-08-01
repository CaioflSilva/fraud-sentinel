CREATE TABLE fraud_analysis (
                                id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                transaction_id      UUID            NOT NULL UNIQUE,
                                rule_score          INTEGER         NOT NULL,
                                ai_score            INTEGER,
                                final_score         INTEGER         NOT NULL,
                                risk_level          VARCHAR(20)     NOT NULL,
                                triggered_rules     JSONB,
                                ai_justification    TEXT,
                                created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),

                                CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id),
                                CONSTRAINT chk_risk_level CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
                                CONSTRAINT chk_rule_score CHECK (rule_score BETWEEN 0 AND 100),
                                CONSTRAINT chk_final_score CHECK (final_score BETWEEN 0 AND 100)
);

CREATE INDEX idx_fraud_analysis_transaction ON fraud_analysis (transaction_id);
CREATE INDEX idx_fraud_analysis_risk_level ON fraud_analysis (risk_level);

COMMENT ON TABLE fraud_analysis IS 'Resultado da analise de fraude por transacao';
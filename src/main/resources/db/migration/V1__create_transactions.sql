CREATE TABLE transactions (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    amount          DECIMAL(19, 4)  NOT NULL,
    currency        VARCHAR(3)      NOT NULL DEFAULT 'BRL',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    description     VARCHAR(255),
    origin_account  VARCHAR(50),
    target_account  VARCHAR(50),
    device_id       VARCHAR(100),
    ip_address      VARCHAR(45),
    location        VARCHAR(100),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_status CHECK (status IN (
        'PENDING', 'ANALYZING', 'RULES_DONE', 'ENRICHED', 'APPROVED', 'FLAGGED'
    )),
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT chk_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_transactions_user_id    ON transactions (user_id);
CREATE INDEX idx_transactions_status     ON transactions (status);
CREATE INDEX idx_transactions_created_at ON transactions (created_at);

COMMENT ON TABLE transactions IS 'Transacoes financeiras submetidas para analise de fraude';
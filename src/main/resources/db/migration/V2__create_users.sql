CREATE TABLE users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    role            VARCHAR(20)     NOT NULL DEFAULT 'ANALYST',
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'ANALYST'))
);

CREATE UNIQUE INDEX idx_users_email ON users (email);

COMMENT ON TABLE users IS 'Usuarios do sistema com autenticacao JWT';
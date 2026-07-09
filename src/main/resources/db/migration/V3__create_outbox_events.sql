CREATE TABLE outbox_events (
                               id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                               aggregate_type  VARCHAR(100)    NOT NULL,
                               event_type      VARCHAR(100)    NOT NULL,
                               payload         JSONB           NOT NULL,
                               published       BOOLEAN         NOT NULL DEFAULT FALSE,
                               created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_outbox_unpublished ON outbox_events (published) WHERE published = FALSE;

COMMENT ON TABLE outbox_events IS 'Transactional Outbox - eventos pendentes de publicacao no Kafka';
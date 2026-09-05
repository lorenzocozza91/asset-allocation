CREATE TABLE broker_fee (
    id TEXT PRIMARY KEY,
    broker_id TEXT NOT NULL,

    fee_type TEXT NOT NULL,
    fee_amount NUMERIC,
    fee_rate_percent NUMERIC,
    minimum_fee NUMERIC,
    maximum_fee NUMERIC,

    currency TEXT NOT NULL DEFAULT 'EUR',
    created_at TEXT NOT NULL,

    FOREIGN KEY (broker_id) REFERENCES broker(id)
);

CREATE TABLE broker (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL UNIQUE,
    created_at TEXT NOT NULL
);

ALTER TABLE asset_transaction ADD COLUMN broker_id TEXT;

CREATE INDEX idx_asset_transaction_broker_id
    ON asset_transaction(broker_id);

-- SQLite does not support adding a foreign-key constraint to an existing table.
-- The application validates broker_id before inserting new transactions.

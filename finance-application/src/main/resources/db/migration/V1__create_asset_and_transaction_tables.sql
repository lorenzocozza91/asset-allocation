CREATE TABLE asset (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    isin TEXT NOT NULL UNIQUE,
    ticker TEXT,
    currency TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE asset_transaction (
    id TEXT PRIMARY KEY NOT NULL,
    asset_id TEXT NOT NULL,
    transaction_type TEXT NOT NULL,
    transaction_date TEXT NOT NULL,
    quantity NUMERIC NOT NULL,
    price NUMERIC NOT NULL,
    currency TEXT NOT NULL,
    fees NUMERIC NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset(id),
    CHECK (transaction_type IN ('BUY', 'SELL')),
    CHECK (quantity > 0),
    CHECK (price >= 0),
    CHECK (fees >= 0)
);

CREATE INDEX idx_asset_transaction_asset_id ON asset_transaction(asset_id);
CREATE INDEX idx_asset_transaction_date ON asset_transaction(transaction_date);

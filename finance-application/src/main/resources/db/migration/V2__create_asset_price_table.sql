CREATE TABLE asset_price (
    id TEXT PRIMARY KEY NOT NULL,
    asset_id TEXT NOT NULL,
    observed_at TEXT NOT NULL,
    price NUMERIC NOT NULL,
    currency TEXT NOT NULL,
    source TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset(id),
    CHECK (price >= 0)
);

CREATE UNIQUE INDEX uq_asset_price_asset_observed_source
    ON asset_price(asset_id, observed_at, COALESCE(source, ''));

CREATE INDEX idx_asset_price_asset_observed_at
    ON asset_price(asset_id, observed_at DESC);

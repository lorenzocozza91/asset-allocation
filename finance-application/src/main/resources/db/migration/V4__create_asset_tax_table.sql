CREATE TABLE asset_tax (
    asset_id TEXT PRIMARY KEY,
    tax_rate_percent NUMERIC NOT NULL,

    FOREIGN KEY (asset_id) REFERENCES asset(id)
);

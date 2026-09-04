package com.lorenzocozza.assetallocation.persistence;

import com.lorenzocozza.assetallocation.domain.AssetPrice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssetPriceRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssetPriceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(AssetPrice assetPrice) {
        jdbcTemplate.update(
                "INSERT INTO asset_price "
                        + "(id, asset_id, observed_at, price, currency, source, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                assetPrice.id().toString(),
                assetPrice.assetId().toString(),
                assetPrice.observedAt().toString(),
                assetPrice.price(),
                assetPrice.currency(),
                assetPrice.source(),
                assetPrice.createdAt().toString());
    }
}

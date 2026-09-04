package com.lorenzocozza.assetallocation.persistence;

import com.lorenzocozza.assetallocation.domain.Asset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AssetRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Asset> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, isin, ticker, currency, created_at, updated_at FROM asset",
                (rs, rowNum) -> new Asset(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("isin"),
                        rs.getString("ticker"),
                        rs.getString("currency"),
                        java.time.Instant.parse(rs.getString("created_at")),
                        java.time.Instant.parse(rs.getString("updated_at"))));
    }
}

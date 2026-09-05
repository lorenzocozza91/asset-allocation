package com.lorenzocozza.assetallocation.persistence;

import com.lorenzocozza.assetallocation.domain.Asset;
import com.lorenzocozza.assetallocation.domain.LatestMarketPrice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssetStateRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssetStateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Asset> findAsset(UUID assetId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT id, name, isin, ticker, currency, created_at, updated_at "
                            + "FROM asset WHERE id = ?",
                    (rs, rowNum) -> new Asset(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("isin"),
                            rs.getString("ticker"),
                            rs.getString("currency"),
                            Instant.parse(rs.getString("created_at")),
                            Instant.parse(rs.getString("updated_at"))),
                    assetId.toString()));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public TransactionTotals findTransactionTotals(UUID assetId) {
        return jdbcTemplate.queryForObject(
                "SELECT "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'BUY' THEN quantity ELSE 0 END), 0) AS bought_quantity, "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'SELL' THEN quantity ELSE 0 END), 0) AS sold_quantity, "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'BUY' THEN quantity * price ELSE 0 END), 0) AS buy_amount, "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'SELL' THEN quantity * price ELSE 0 END), 0) AS sell_amount, "
                        + "COALESCE(SUM(fees), 0) AS total_fees, "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'BUY' THEN fees ELSE 0 END), 0) AS buy_fees, "
                        + "COALESCE(SUM(CASE WHEN transaction_type = 'SELL' THEN fees ELSE 0 END), 0) AS sell_fees "
                        + "FROM asset_transaction WHERE asset_id = ?",
                (rs, rowNum) -> new TransactionTotals(
                        decimal(rs.getString("bought_quantity")),
                        decimal(rs.getString("sold_quantity")),
                        decimal(rs.getString("buy_amount")),
                        decimal(rs.getString("sell_amount")),
                        decimal(rs.getString("total_fees")),
                        decimal(rs.getString("buy_fees")),
                        decimal(rs.getString("sell_fees"))),
                assetId.toString());
    }

    public Optional<LatestMarketPrice> findLatestPrice(UUID assetId) {
        return jdbcTemplate.query(
                        "SELECT price, observed_at, currency, source FROM asset_price "
                                + "WHERE asset_id = ? ORDER BY observed_at DESC LIMIT 1",
                        (rs, rowNum) -> new LatestMarketPrice(
                                decimal(rs.getString("price")),
                                Instant.parse(rs.getString("observed_at")),
                                rs.getString("currency"),
                                rs.getString("source")),
                        assetId.toString())
                .stream()
                .findFirst();
    }

    public BigDecimal findTaxRatePercent(UUID assetId) {
        return jdbcTemplate.query(
                        "SELECT tax_rate_percent FROM asset_tax WHERE asset_id = ?",
                        (rs, rowNum) -> decimal(rs.getString("tax_rate_percent")),
                        assetId.toString())
                .stream()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal decimal(String value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public record TransactionTotals(
            BigDecimal boughtQuantity,
            BigDecimal soldQuantity,
            BigDecimal totalBuyAmount,
            BigDecimal totalSellAmount,
            BigDecimal totalFees,
            BigDecimal buyFees,
            BigDecimal sellFees) {
    }
}

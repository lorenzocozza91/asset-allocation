package com.lorenzocozza.assetallocation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class AssetAllocationApplicationTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsAndMigrationsCreateFinanceTables() {
        assertEquals(3, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' "
                        + "AND name IN ('asset', 'asset_transaction', 'asset_price')",
                Integer.class));
    }
}

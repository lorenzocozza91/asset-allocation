package com.lorenzocozza.assetallocation.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssetPrice(
        UUID id,
        UUID assetId,
        Instant observedAt,
        BigDecimal price,
        String currency,
        String source,
        Instant createdAt) {
}

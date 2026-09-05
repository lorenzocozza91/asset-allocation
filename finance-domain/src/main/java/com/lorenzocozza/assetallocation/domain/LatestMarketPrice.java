package com.lorenzocozza.assetallocation.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record LatestMarketPrice(
        BigDecimal price,
        Instant observedAt,
        String currency,
        String source) {
}

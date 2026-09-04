package com.lorenzocozza.assetallocation.domain.marketdata;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketQuote(
        Instant observedAt,
        BigDecimal price,
        String currency,
        String source) {
}

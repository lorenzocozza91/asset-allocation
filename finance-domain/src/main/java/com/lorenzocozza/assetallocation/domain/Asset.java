package com.lorenzocozza.assetallocation.domain;

import java.time.Instant;
import java.util.UUID;

public record Asset(
        UUID id,
        String name,
        String isin,
        String ticker,
        String currency,
        Instant createdAt,
        Instant updatedAt) {
}

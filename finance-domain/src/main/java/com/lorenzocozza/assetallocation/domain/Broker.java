package com.lorenzocozza.assetallocation.domain;

import java.time.Instant;
import java.util.UUID;

public record Broker(
        UUID id,
        String name,
        Instant createdAt) {
}

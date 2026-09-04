package com.lorenzocozza.assetallocation.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AssetTransaction(
        UUID id,
        UUID assetId,
        TransactionType transactionType,
        LocalDate transactionDate,
        BigDecimal quantity,
        BigDecimal price,
        String currency,
        BigDecimal fees,
        String notes,
        Instant createdAt) {
}

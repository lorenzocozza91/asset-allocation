package com.lorenzocozza.assetallocation.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssetState(
        UUID assetId,
        String name,
        String isin,
        String ticker,
        String currency,
        BigDecimal boughtQuantity,
        BigDecimal soldQuantity,
        BigDecimal currentQuantity,
        BigDecimal totalBuyAmount,
        BigDecimal totalSellAmount,
        BigDecimal totalFees,
        BigDecimal investedAmount,
        BigDecimal averageBuyPrice,
        BigDecimal latestPrice,
        Instant latestPriceTimestamp,
        String priceSource,
        BigDecimal currentValue,
        BigDecimal unrealizedProfit,
        BigDecimal unrealizedProfitPercentage,
        BigDecimal expectedTax,
        BigDecimal profitWithoutTax,
        BigDecimal profitWithoutTaxAndFees) {
}

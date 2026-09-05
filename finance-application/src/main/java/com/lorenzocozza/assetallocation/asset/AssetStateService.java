package com.lorenzocozza.assetallocation.asset;

import com.lorenzocozza.assetallocation.domain.Asset;
import com.lorenzocozza.assetallocation.domain.AssetState;
import com.lorenzocozza.assetallocation.domain.LatestMarketPrice;
import com.lorenzocozza.assetallocation.persistence.AssetStateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssetStateService {

    private final AssetStateRepository assetStateRepository;

    public AssetStateService(AssetStateRepository assetStateRepository) {
        this.assetStateRepository = assetStateRepository;
    }

    public Optional<AssetState> getState(UUID assetId) {
        return assetStateRepository.findAsset(assetId)
                .map(asset -> calculate(asset, assetStateRepository.findTransactionTotals(assetId),
                        assetStateRepository.findLatestPrice(assetId).orElse(null)));
    }

    private AssetState calculate(
            Asset asset,
            AssetStateRepository.TransactionTotals totals,
            LatestMarketPrice latestPrice) {
        BigDecimal currentQuantity = totals.boughtQuantity().subtract(totals.soldQuantity());
        BigDecimal investedAmount = totals.totalBuyAmount()
                .add(totals.buyFees())
                .subtract(totals.totalSellAmount())
                .add(totals.sellFees());
        BigDecimal averageBuyPrice = divide(totals.totalBuyAmount(), totals.boughtQuantity());

        BigDecimal currentValue = null;
        BigDecimal unrealizedProfit = null;
        BigDecimal unrealizedProfitPercentage = null;
        BigDecimal latestPriceValue = null;

        if (latestPrice != null) {
            latestPriceValue = latestPrice.price();
            currentValue = currentQuantity.multiply(latestPrice.price());
            BigDecimal costBasis = currentQuantity.multiply(averageBuyPrice);
            unrealizedProfit = currentValue.subtract(costBasis);
            unrealizedProfitPercentage = percentage(unrealizedProfit, costBasis);
        }

        return new AssetState(
                asset.id(),
                asset.name(),
                asset.isin(),
                asset.ticker(),
                asset.currency(),
                totals.boughtQuantity(),
                totals.soldQuantity(),
                currentQuantity,
                totals.totalBuyAmount(),
                totals.totalSellAmount(),
                totals.totalFees(),
                investedAmount,
                averageBuyPrice,
                latestPriceValue,
                latestPrice == null ? null : latestPrice.observedAt(),
                latestPrice == null ? null : latestPrice.source(),
                currentValue,
                unrealizedProfit,
                unrealizedProfitPercentage);
    }

    private static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return divisor.signum() == 0
                ? BigDecimal.ZERO
                : dividend.divide(divisor, 8, RoundingMode.HALF_UP);
    }

    private static BigDecimal percentage(BigDecimal value, BigDecimal base) {
        return base.signum() == 0
                ? BigDecimal.ZERO
                : value.multiply(BigDecimal.valueOf(100))
                .divide(base, 4, RoundingMode.HALF_UP);
    }
}

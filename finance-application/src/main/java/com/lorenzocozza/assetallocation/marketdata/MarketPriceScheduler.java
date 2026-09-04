package com.lorenzocozza.assetallocation.marketdata;

import com.lorenzocozza.assetallocation.domain.AssetPrice;
import com.lorenzocozza.assetallocation.domain.marketdata.MarketDataProvider;
import com.lorenzocozza.assetallocation.persistence.AssetPriceRepository;
import com.lorenzocozza.assetallocation.persistence.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MarketPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceScheduler.class);

    private final AssetRepository assetRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final MarketDataProvider marketDataProvider;

    public MarketPriceScheduler(
            AssetRepository assetRepository,
            AssetPriceRepository assetPriceRepository,
            MarketDataProvider marketDataProvider) {
        this.assetRepository = assetRepository;
        this.assetPriceRepository = assetPriceRepository;
        this.marketDataProvider = marketDataProvider;
    }

    @Scheduled(
            fixedRateString = "${market-data.schedule.interval-ms:3600000}",
            initialDelayString = "${market-data.schedule.initial-delay-ms:0}")
    public void storeLatestPrices() {
        long startedAt = System.nanoTime();
        int processed = 0;
        int saved = 0;
        int unavailable = 0;
        int skipped = 0;

        log.info("Starting scheduled market price update");

        for (var asset : assetRepository.findAll()) {
            if (asset.ticker() == null || asset.ticker().isBlank()) {
                skipped++;
                log.debug("Skipping asset {} because it has no ticker", asset.id());
                continue;
            }

            processed++;
            try {
                var quote = marketDataProvider.fetchLatestQuote(asset);
                if (quote.isEmpty()) {
                    unavailable++;
                    log.warn("No market price available for asset {} ({})", asset.id(), asset.ticker());
                    continue;
                }

                var marketQuote = quote.get();
                assetPriceRepository.save(new AssetPrice(
                        UUID.randomUUID(),
                        asset.id(),
                        marketQuote.observedAt(),
                        marketQuote.price(),
                        marketQuote.currency(),
                        marketQuote.source(),
                        Instant.now()));
                saved++;
                log.debug("Stored market price {} {} for asset {} ({})",
                        marketQuote.price(), marketQuote.currency(), asset.id(), asset.ticker());
            } catch (RuntimeException exception) {
                unavailable++;
                log.error("Failed to update market price for asset {} ({})",
                        asset.id(), asset.ticker(), exception);
            }
        }

        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
        log.info("Completed scheduled market price update: processed={}, saved={}, unavailable={}, "
                        + "skipped={}, durationMs={}",
                processed, saved, unavailable, skipped, durationMs);
    }
}

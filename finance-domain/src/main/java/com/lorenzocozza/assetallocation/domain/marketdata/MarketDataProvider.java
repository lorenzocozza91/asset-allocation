package com.lorenzocozza.assetallocation.domain.marketdata;

import com.lorenzocozza.assetallocation.domain.Asset;

import java.util.Optional;

public interface MarketDataProvider {

    /**
     * Loads the most recent available market quote for an asset.
     *
     * @param asset asset metadata used by the provider to identify the instrument
     * @return the latest quote, or empty when the provider has no data
     */
    Optional<MarketQuote> fetchLatestQuote(Asset asset);
}

package com.lorenzocozza.assetallocation.marketdata;

import com.lorenzocozza.assetallocation.domain.Asset;
import com.lorenzocozza.assetallocation.domain.marketdata.MarketDataProvider;
import com.lorenzocozza.assetallocation.domain.marketdata.MarketQuote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.Instant;
import java.net.URI;
import java.util.Optional;

@Component
public class YahooFinanceMarketDataProvider implements MarketDataProvider {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceMarketDataProvider.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0 Safari/537.36";
    private static final int MAX_ATTEMPTS = 3;

    private final String baseUrl;

    public YahooFinanceMarketDataProvider(
            @Value("${market-data.yahoo-finance.base-url:https://query1.finance.yahoo.com}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<MarketQuote> fetchLatestQuote(Asset asset) {
        if (asset == null || asset.ticker() == null || asset.ticker().isBlank()) {
            return Optional.empty();
        }

        URI requestUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/v8/finance/chart/{symbol}")
                .queryParam("range", "1d")
                .queryParam("interval", "1m")
                .buildAndExpand(asset.ticker())
                .encode()
                .toUri();

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                log.info("Requesting Yahoo Finance quote for asset {}: {} (attempt {}/{})",
                        asset.id(), requestUri, attempt, MAX_ATTEMPTS);

                JsonNode response = RestClient.create().get()
                        .uri(requestUri)
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "application/json")
                        .retrieve()
                        .body(JsonNode.class);

                return toMarketQuote(response);
            } catch (RestClientResponseException exception) {
                if (exception.getStatusCode().value() != 429 || attempt == MAX_ATTEMPTS) {
                    log.warn("Yahoo Finance request failed for asset {}: {}", asset.id(), requestUri, exception);
                    return Optional.empty();
                }

                long backoffMs = 1_000L * (1L << (attempt - 1));
                log.warn("Yahoo Finance rate-limited asset {}. Retrying in {} ms", asset.id(), backoffMs);
                if (!sleepBeforeRetry(backoffMs)) {
                    return Optional.empty();
                }
            } catch (RestClientException exception) {
                log.warn("Yahoo Finance request failed for asset {}: {}", asset.id(), requestUri, exception);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private boolean sleepBeforeRetry(long backoffMs) {
        try {
            Thread.sleep(backoffMs);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("Yahoo Finance retry interrupted", exception);
            return false;
        }
    }

    private Optional<MarketQuote> toMarketQuote(JsonNode response) {
        JsonNode meta = response == null
                ? null
                : response.path("chart").path("result").path(0).path("meta");

        if (meta == null || meta.isMissingNode()) {
            return Optional.empty();
        }

        JsonNode priceNode = meta.path("regularMarketPrice");
        JsonNode timestampNode = meta.path("regularMarketTime");
        String currency = meta.path("currency").asText(null);

        if (!priceNode.isNumber() || !timestampNode.isIntegralNumber() || currency == null) {
            return Optional.empty();
        }

        BigDecimal price = priceNode.decimalValue();
        Instant observedAt = Instant.ofEpochSecond(timestampNode.longValue());
        return Optional.of(new MarketQuote(observedAt, price, currency, "YAHOO_FINANCE"));
    }
}

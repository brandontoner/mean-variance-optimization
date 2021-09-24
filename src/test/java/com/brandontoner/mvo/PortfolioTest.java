package com.brandontoner.mvo;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PortfolioTest {
    private final Ticker[] tickers = new Ticker[0];
    private final int[] counts = new int[0];
    private final double mean = ThreadLocalRandom.current().nextDouble();
    private final double variance = ThreadLocalRandom.current().nextDouble();
    private final Portfolio portfolio = new Portfolio(tickers, counts, mean, variance);

    @Test
    void tickers() {
        Assertions.assertSame(tickers, portfolio.tickers());
    }

    @Test
    void counts() {
        Assertions.assertSame(counts, portfolio.counts());
    }

    @Test
    void mean() {
        Assertions.assertEquals(mean, portfolio.mean());
    }

    @Test
    void variance() {
        Assertions.assertEquals(variance, portfolio.variance());
    }

    @Test
    void countsMap_empty() {
        Assertions.assertEquals(Map.of(), portfolio.countsMap());
    }

    @Test
    void countsMap() {
        Portfolio portfolio = new Portfolio(new Ticker[] {new Ticker("foo", Map.of())},
                                            new int[] {1234},
                                            mean,
                                            variance);

        Assertions.assertEquals(Map.of("foo", 1234), portfolio.countsMap());
    }
}
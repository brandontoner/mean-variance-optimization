package com.brandontoner.mvo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A collection of tickers and their allocations.
 */
public final class Portfolio {
    /** Tickers, each value corresponds to the same index in counts. */
    private final Ticker[] tickers;
    /** Count of each ticker, each value corresponds to the same index in tickers. */
    private final int[] counts;
    /** Arithmetic mean of the returns. */
    private final double mean;
    /** Variance of the returns. */
    private final double variance;

    /**
     * Constructor.
     *
     * @param tickers  tickers, each value corresponds to the same index in counts.
     * @param counts   count of each ticker, each value corresponds to the same index in tickers.
     * @param mean     arithmetic mean of the returns
     * @param variance variance of the returns
     */
    Portfolio(Ticker[] tickers, int[] counts, double mean, double variance) {
        this.tickers = tickers;
        this.counts = counts;
        this.mean = mean;
        this.variance = variance;
    }

    /**
     * Gets the array of tickers for this portfolio.
     *
     * @return the array of tickers for this portfolio. Each value corresponds to the same index in counts.
     */
    public Ticker[] tickers() {
        return tickers;
    }

    /**
     * Gets the array of counts for this portfolio.
     *
     * @return the array of counts for this portfolio. Each value corresponds to the same index in tickers.
     */
    public int[] counts() {
        return counts;
    }

    /**
     * Gets the arithmetic mean of the returns.
     *
     * @return arithmetic mean of the returns
     */
    public double mean() {
        return mean;
    }

    /**
     * Gets the variance of the returns.
     *
     * @return variance of the returns
     */
    public double variance() {
        return variance;
    }

    /**
     * Gets a map of ticker name to count.
     *
     * @return map of ticker name to count.
     */
    public Map<String, Integer> countsMap() {
        Map<String, Integer> out = new HashMap<>();
        for (int i = 0; i < tickers.length; i++) {
            int count = counts[i];
            if (count != 0) {
                out.put(tickers[i].getName(), count);
            }
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Portfolio portfolio = (Portfolio) o;
        return Double.compare(portfolio.mean, mean) == 0
               && Double.compare(portfolio.variance, variance) == 0
               && Arrays.equals(tickers, portfolio.tickers)
               && Arrays.equals(counts, portfolio.counts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mean, variance);
        result = 31 * result + Arrays.hashCode(tickers);
        result = 31 * result + Arrays.hashCode(counts);
        return result;
    }

    @Override
    public String toString() {
        return "Portfolio{tickers=%s, counts=%s, mean=%s, variance=%s}".formatted(Arrays.toString(tickers),
                                                                                  Arrays.toString(counts),
                                                                                  mean,
                                                                                  variance);
    }
}

package com.brandontoner.mvo;

import java.util.Arrays;

public final class PortfolioFactory {
    private static final ThreadLocal<double[]> RETURNS_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<double[]> CLOSING_PRICES_THREAD_LOCAL = new ThreadLocal<>();

    private PortfolioFactory() {
    }

    /**
     * Gets a Portfolio for the provided ticker and count arrays.
     *
     * @param tickers ticker array
     * @param counts  count array
     * @return portfolio
     */
    public static Portfolio get(Ticker[] tickers, int[] counts) {
        double[] closingValues = getClosingPrices(tickers, counts);
        double[] returns = getReturns(closingValues);
        double mean = Statistics.mean(returns);
        double variance = Statistics.variance(returns, mean);
        return new Portfolio(tickers, counts, mean, variance);
    }

    static double[] getReturns(double[] closingValues) {
        double[] returns = RETURNS_THREAD_LOCAL.get();
        if (returns == null || returns.length != closingValues.length - 1) {
            returns = new double[closingValues.length - 1];
            RETURNS_THREAD_LOCAL.set(returns);
        }
        double last = closingValues[0];
        for (int i = 1; i < closingValues.length; ++i) {
            double v = closingValues[i];
            returns[i - 1] = v / last;
            last = v;
        }
        return returns;
    }

    private static double[] getClosingPrices(Ticker[] tickers, int[] counts) {
        double[] output = CLOSING_PRICES_THREAD_LOCAL.get();
        int length = tickers[0].getClosingPricesArray().length;
        if (output == null || output.length != length) {
            output = new double[length];
            CLOSING_PRICES_THREAD_LOCAL.set(output);
        }
        Arrays.fill(output, 0);
        for (int i = 0; i < tickers.length; i++) {
            int coef = counts[i];
            if (coef == 0) {
                continue;
            }
            Ticker ticker = tickers[i];
            double[] closingPricesArray = ticker.getClosingPricesArray(coef);

            for (int j = 0; j < closingPricesArray.length; j++) {
                output[j] += closingPricesArray[j];
            }
        }
        return output;
    }
}

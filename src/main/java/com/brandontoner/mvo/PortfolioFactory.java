package com.brandontoner.mvo;

public final class PortfolioFactory {
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

    private static double[] getReturns(double[] closingValues) {
        double[] returns = new double[closingValues.length - 1];
        for (int i = 1; i < closingValues.length; ++i) {
            returns[i - 1] = closingValues[i] / closingValues[i - 1];
        }
        return returns;
    }


    private static double[] getClosingPrices(Ticker[] tickers, int[] counts) {
        double[] output = new double[tickers[0].getClosingPricesArray().length];
        for (int i = 0; i < tickers.length; i++) {
            int coef = counts[i];
            if (coef == 0) {
                continue;
            }
            Ticker ticker = tickers[i];
            double[] closingPricesArray = ticker.getClosingPricesArray();
            for (int j = 0; j < closingPricesArray.length; j++) {
                output[j] += closingPricesArray[j] * coef;
            }
        }
        return output;
    }
}

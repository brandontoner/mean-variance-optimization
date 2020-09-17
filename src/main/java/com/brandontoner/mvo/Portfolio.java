package com.brandontoner.mvo;

public abstract class Portfolio {
    /**
     * Gets the arithmetic mean period-over-period (as a ratio) return of this portfolio.
     *
     * @return arithmetic mean period-over-period return of this portfolio
     */
    public abstract double mean();

    /**
     * Gets the variance of the period-over-period returns of this portfolio.
     *
     * @return variance of the period-over-period returns of this portfolio.
     */
    public abstract double variance();

    @Override
    public String toString() {
        return "{\"mean\": " + mean() + ", " + "\"variance\": " + variance() + "}";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Portfolio)) {
            return false;
        }

        Portfolio portfolio = (Portfolio) o;

        return Double.compare(portfolio.mean(), mean()) == 0 && Double.compare(portfolio.variance(), variance()) == 0;
    }

    @Override
    public final int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mean());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(variance());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

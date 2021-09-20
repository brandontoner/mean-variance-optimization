package com.brandontoner.mvo;

class PortfolioImpl extends Portfolio {
    private final double mean;
    private final double variance;

    PortfolioImpl(double mean, double variance) {
        this.mean = mean;
        this.variance = variance;
    }

    @Override
    public double mean() {
        return mean;
    }

    @Override
    public double variance() {
        return variance;
    }
}

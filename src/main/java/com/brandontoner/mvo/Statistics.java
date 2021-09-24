package com.brandontoner.mvo;

public class Statistics {
    /**
     * Computes the Arithmetic Mean of the provided array.
     *
     * @param doubles non-null array
     * @return arithmetic mean
     */
    public static double mean(double[] doubles) {
        double sum = 0;
        long count = 0;
        for (double v : doubles) {
            sum += v;
            count++;
        }
        return sum / count;
    }

    /**
     * Computes the variance of an array.
     *
     * @param doubles non-null array
     * @param mean    arithmetic mean
     * @return variance
     * @see #mean(double[])
     */
    public static double variance(double[] doubles, double mean) {
        double variance = 0;
        for (double v : doubles) {
            double delta = v - mean;
            variance += delta * delta;
        }
        return variance / doubles.length;
    }
}

package com.brandontoner.mvo;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class Statistics {
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    /**
     * Computes the Arithmetic Mean of the provided array.
     *
     * @param doubles non-null array
     * @return arithmetic mean
     */
    public static double mean(double[] doubles) {
        double sum = 0;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(doubles.length);
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            sum += DoubleVector.fromArray(DOUBLE_SPECIES, doubles, i).reduceLanes(VectorOperators.ADD);
        }
        for (; i < doubles.length; i++) {
            sum += doubles[i];
        }
        return sum / doubles.length;
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
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(doubles.length);
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            var delta = DoubleVector.fromArray(DOUBLE_SPECIES, doubles, i).sub(mean);
            variance += delta.mul(delta).reduceLanes(VectorOperators.ADD);
        }

        for (; i < doubles.length; i++) {
            double delta = doubles[i] - mean;
            variance += delta * delta;
        }
        return variance / doubles.length;
    }
}

package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import org.junit.jupiter.api.Test;

class StatisticsTest {
    @Test
    void mean() {
        double[] random = randomDoubles(10);
        assertEquals(DoubleStream.of(random).average().getAsDouble(), Statistics.mean(random), 0.0000001);
    }

    @Test
    void variance() {
        double[] random = randomDoubles(10);
        double mean = DoubleStream.of(random).average().getAsDouble();
        double var = DoubleStream.of(random).map(v -> v - mean).map(v -> v * v).average().getAsDouble();
        assertEquals(var, Statistics.variance(random, mean), 0.0000001);
    }

    private double[] randomDoubles(int n) {
        return ThreadLocalRandom.current().doubles(n).toArray();
    }
}
package com.brandontoner.mvo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.infra.Blackhole;

public class StatisticsBenchmark {
    private static final double[] DOUBLES = ThreadLocalRandom.current().doubles(1000).toArray();
    private static final double DOUBLES_MEAN = DoubleStream.of(DOUBLES).average().getAsDouble();
    private static final int FORK = 1;

    @Fork(FORK)
    @Benchmark
    public void mean(Blackhole blackhole) {
        blackhole.consume(Statistics.mean(DOUBLES));
    }


    @Fork(FORK)
    @Benchmark
    public void variance(Blackhole blackhole) {
        blackhole.consume(Statistics.variance(DOUBLES, DOUBLES_MEAN));
    }
}

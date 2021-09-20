package com.brandontoner.mvo;

import static com.brandontoner.mvo.MeanVarianceSetCollector.INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.junit.jupiter.api.Test;

class MeanVarianceSetCollectorTest {

    @Test
    void supplier() {
        Supplier<MeanVarianceSet> supplier = INSTANCE.supplier();
        assertEquals(new MeanVarianceSet(), supplier.get());
    }

    @Test
    void accumulator() {
        MeanVarianceSet set = new MeanVarianceSet();
        Portfolio portfolio = getPortfolio(0, 0);
        INSTANCE.accumulator().accept(set, portfolio);
        assertEquals(new MeanVarianceSet(List.of(portfolio)), set);
    }

    @Test
    void combiner_oneEmpty() {
        MeanVarianceSet nonEmpty = new MeanVarianceSet(List.of(getPortfolio(0, 0)));
        MeanVarianceSet empty = new MeanVarianceSet();
        assertSame(nonEmpty, INSTANCE.combiner().apply(nonEmpty, empty));
        assertSame(nonEmpty, INSTANCE.combiner().apply(empty, nonEmpty));
    }

    @Test
    void combiner_nonEmpty() {
        Portfolio p1 = getPortfolio(0, 0);
        Portfolio p2 = getPortfolio(1, 1);
        Portfolio p3 = getPortfolio(2, 2);
        MeanVarianceSet set1 = new MeanVarianceSet(List.of(p1, p2));
        MeanVarianceSet set2 = new MeanVarianceSet(List.of(p2, p3));
        assertEquals(new MeanVarianceSet(List.of(p1, p2, p3)), INSTANCE.combiner().apply(set1, set2));
    }

    @Test
    void finisher() {
        MeanVarianceSet set = new MeanVarianceSet();
        assertSame(set, INSTANCE.finisher().apply(set));
    }

    @Test
    void characteristics() {
        assertEquals(Set.of(Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED),
                     INSTANCE.characteristics());
    }

    private Portfolio getPortfolio(int mean, int variance) {
        return new Portfolio() {
            @Override
            public double mean() {
                return mean;
            }

            @Override
            public double variance() {
                return variance;
            }
        };
    }
}

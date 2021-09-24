package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.junit.jupiter.api.Test;

class MeanVarianceSetCollectorTest {
    private final MeanVarianceSetCollector instance = new MeanVarianceSetCollector();

    @Test
    void supplier() {
        Supplier<MeanVarianceSet> supplier = instance.supplier();
        assertEquals(new MeanVarianceSet(), supplier.get());
    }

    @Test
    void accumulator() {
        MeanVarianceSet set = new MeanVarianceSet();
        Portfolio portfolio = createPortfolio(0, 0);
        instance.accumulator().accept(set, portfolio);
        assertEquals(new MeanVarianceSet(List.of(portfolio)), set);
    }

    @Test
    void combiner_oneEmpty() {
        MeanVarianceSet nonEmpty = new MeanVarianceSet(List.of(createPortfolio(0, 0)));
        MeanVarianceSet empty = new MeanVarianceSet();
        assertSame(nonEmpty, instance.combiner().apply(nonEmpty, empty));
        assertSame(nonEmpty, instance.combiner().apply(empty, nonEmpty));
    }

    @Test
    void combiner_nonEmpty() {
        Portfolio p1 = createPortfolio(0, 0);
        Portfolio p2 = createPortfolio(1, 1);
        Portfolio p3 = createPortfolio(2, 2);
        MeanVarianceSet set1 = new MeanVarianceSet(List.of(p1, p2));
        MeanVarianceSet set2 = new MeanVarianceSet(List.of(p2, p3));
        assertEquals(new MeanVarianceSet(List.of(p1, p2, p3)), instance.combiner().apply(set1, set2));
    }

    @Test
    void finisher() {
        MeanVarianceSet set = new MeanVarianceSet();
        assertSame(set, instance.finisher().apply(set));
    }

    @Test
    void characteristics() {
        assertEquals(Set.of(Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED),
                     instance.characteristics());
    }

    private static Portfolio createPortfolio(int i, int i1) {
        return new Portfolio(new Ticker[0], new int[0], i, i1);
    }
}

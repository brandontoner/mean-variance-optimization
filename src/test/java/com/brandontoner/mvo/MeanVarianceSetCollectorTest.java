package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.junit.jupiter.api.Test;

class MeanVarianceSetCollectorTest { 
    private final MeanVarianceSetCollector<PortfolioImpl> instance = new MeanVarianceSetCollector<>();

    @Test
    void supplier() {
        Supplier<MeanVarianceSet<PortfolioImpl>> supplier = instance.supplier();
        assertEquals(new MeanVarianceSet<>(), supplier.get());
    }

    @Test
    void accumulator() {
        MeanVarianceSet<PortfolioImpl> set = new MeanVarianceSet<>();
        PortfolioImpl portfolio = new PortfolioImpl(0, 0);
        instance.accumulator().accept(set, portfolio);
        assertEquals(new MeanVarianceSet<>(List.of(portfolio)), set);
    }

    @Test
    void combiner_oneEmpty() {
        MeanVarianceSet<PortfolioImpl> nonEmpty = new MeanVarianceSet<>(List.of(new PortfolioImpl(0, 0)));
        MeanVarianceSet<PortfolioImpl> empty = new MeanVarianceSet<>();
        assertSame(nonEmpty, instance.combiner().apply(nonEmpty, empty));
        assertSame(nonEmpty, instance.combiner().apply(empty, nonEmpty));
    }

    @Test
    void combiner_nonEmpty() {
        PortfolioImpl p1 = new PortfolioImpl(0, 0);
        PortfolioImpl p2 = new PortfolioImpl(1, 1);
        PortfolioImpl p3 = new PortfolioImpl(2, 2);
        MeanVarianceSet<PortfolioImpl> set1 = new MeanVarianceSet<>(List.of(p1, p2));
        MeanVarianceSet<PortfolioImpl> set2 = new MeanVarianceSet<>(List.of(p2, p3));
        assertEquals(new MeanVarianceSet<>(List.of(p1, p2, p3)), instance.combiner().apply(set1, set2));
    }

    @Test
    void finisher() {
        MeanVarianceSet<PortfolioImpl> set = new MeanVarianceSet<>();
        assertSame(set, instance.finisher().apply(set));
    }

    @Test
    void characteristics() {
        assertEquals(Set.of(Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED),
                     instance.characteristics());
    }
}

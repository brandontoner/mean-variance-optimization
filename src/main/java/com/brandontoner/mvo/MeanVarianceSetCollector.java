package com.brandontoner.mvo;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class MeanVarianceSetCollector<T extends Portfolio> implements Collector<T, MeanVarianceSet<T>, MeanVarianceSet<T>> {
    private static final Set<Characteristics> CHARACTERISTICS =
            Set.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);

    @Override
    public Supplier<MeanVarianceSet<T>> supplier() {
        return MeanVarianceSet::new;
    }

    @Override
    public BiConsumer<MeanVarianceSet<T>, T> accumulator() {
        return MeanVarianceSet::add;
    }

    @Override
    public BinaryOperator<MeanVarianceSet<T>> combiner() {
        return (a, b) -> {
            if (a.isEmpty()) {
                return b;
            }
            if (b.isEmpty()) {
                return a;
            }
            if (a.size() < b.size()) {
                b.addAll(a); // a is smaller
                return b;
            } else {
                a.addAll(b);
                return a;
            }
        };
    }

    @Override
    public Function<MeanVarianceSet<T>, MeanVarianceSet<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return CHARACTERISTICS;
    }
}

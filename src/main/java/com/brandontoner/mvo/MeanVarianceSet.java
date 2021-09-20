package com.brandontoner.mvo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collector;

/**
 * A Collection of portfolios which satisfy the mean variance optimization constraints.
 * <p>
 * For every pair (a, b) in the collection, where a != b, a.mean() < b.mean() <=> a.variance() < b.variance()
 * </p>
 */
public class MeanVarianceSet<T extends Portfolio> implements Collection<T> {
    /** Backing list. */
    private final List<T> portfolios = new ArrayList<>();

    public MeanVarianceSet() {
        // noop
    }

    public MeanVarianceSet(Collection<T> expected) {
        addAll(expected);
    }

    @Override
    public int size() {
        return portfolios.size();
    }

    @Override
    public boolean isEmpty() {
        return portfolios.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return portfolios.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return portfolios.iterator();
    }

    @Override
    public Object[] toArray() {
        return portfolios.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return portfolios.toArray(a);
    }

    @Override
    public boolean add(T portfolio) {
        int index = Collections.binarySearch(portfolios, portfolio, Comparator.comparingDouble(T::mean));
        if (index >= 0) {
            T other = portfolios.get(index);
            if (portfolio.variance() < other.variance()) {
                portfolios.set(index, portfolio);
                return true;
            } else {
                return false;
            }
        }
        index = -(index + 1);
        if (index > 0) {
            T other = portfolios.get(index - 1);
            if (portfolio.variance() <= other.variance()) {
                portfolios.set(index - 1, portfolio);
                while (index - 2 >= 0) {
                    T p = portfolios.get(index - 2);
                    if (portfolio.variance() <= p.variance()) {
                        portfolios.remove(index - 2);
                        index--;
                    } else {
                        break;
                    }
                }
                return true;
            }
        }
        if (index < portfolios.size()) {
            T other = portfolios.get(index);
            if (portfolio.variance() >= other.variance()) {
                return false;
            }
        }
        portfolios.add(index, portfolio);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return portfolios.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return portfolios.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T portfolio : c) {
            if (add(portfolio)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return portfolios.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return portfolios.retainAll(c);
    }

    @Override
    public void clear() {
        portfolios.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MeanVarianceSet<?> that = (MeanVarianceSet<?>) o;

        return portfolios.equals(that.portfolios);
    }

    @Override
    public int hashCode() {
        return portfolios.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return portfolios.spliterator();
    }

    /**
     * Gets a Collector for use with {@link java.util.stream.Stream#collect(Collector)}.
     *
     * @return collector which collects into a MeanVarianceSet
     */
    public static <T extends Portfolio> Collector<T, ?, MeanVarianceSet<T>> collector() {
        return new MeanVarianceSetCollector<>();
    }
}

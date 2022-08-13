package com.brandontoner.mvo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import javax.annotation.Nonnull;

/**
 * A Collection of portfolios which satisfy the mean variance optimization constraints.
 * <p>
 * For every pair (a, b) in the collection, where a != b, a.mean() < b.mean() <=> a.variance() < b.variance()
 * </p>
 */
public class MeanVarianceSet implements Collection<Portfolio> {
    /** Backing list. */
    private final List<Portfolio> portfolios = new ArrayList<>();

    public MeanVarianceSet() {
        // noop
    }

    public MeanVarianceSet(Collection<Portfolio> expected) {
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
    public Iterator<Portfolio> iterator() {
        return portfolios.iterator();
    }

    @Override
    public Object[] toArray() {
        return portfolios.toArray();
    }

    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return portfolios.toArray(a);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
     */
    public Portfolio get(int index) {
        return portfolios.get(index);
    }

    @Override
    public boolean add(Portfolio portfolio) {
        int index = Collections.binarySearch(portfolios, portfolio, Comparator.comparingDouble(Portfolio::mean));
        if (index >= 0) {
            Portfolio other = portfolios.get(index);
            if (portfolio.variance() < other.variance()) {
                portfolios.set(index, portfolio);
                return true;
            } else {
                return false;
            }
        }
        index = -(index + 1);
        if (index > 0) {
            Portfolio other = portfolios.get(index - 1);
            if (portfolio.variance() <= other.variance()) {
                portfolios.set(index - 1, portfolio);
                while (index - 2 >= 0) {
                    Portfolio p = portfolios.get(index - 2);
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
            Portfolio other = portfolios.get(index);
            if (portfolio.variance() >= other.variance()) {
                return false;
            }
        }
        portfolios.add(index, portfolio);
        return true;
    }

    /**
     * Adds a portfolio to the set. Avoids allocating the {@link Portfolio} if not being inserted.
     *
     * @param tickers  portfolio tickers
     * @param counts   portfolio counts, will be cloned
     * @param mean     portfolio mean
     * @param variance portfolio variance
     * @return true if added, else false
     */
    public boolean add(Ticker[] tickers, int[] counts, double mean, double variance) {
        int index = binarySearch(portfolios, mean, Portfolio::mean);
        if (index >= 0) {
            Portfolio other = portfolios.get(index);
            if (variance < other.variance()) {
                portfolios.set(index, new Portfolio(tickers, counts.clone(), mean, variance));
                return true;
            } else {
                return false;
            }
        }
        index = -(index + 1);
        if (index > 0) {
            Portfolio other = portfolios.get(index - 1);
            if (variance <= other.variance()) {
                portfolios.set(index - 1, new Portfolio(tickers, counts.clone(), mean, variance));
                while (index - 2 >= 0) {
                    Portfolio p = portfolios.get(index - 2);
                    if (variance <= p.variance()) {
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
            Portfolio other = portfolios.get(index);
            if (variance >= other.variance()) {
                return false;
            }
        }
        portfolios.add(index, new Portfolio(tickers, counts.clone(), mean, variance));
        return true;
    }

    /**
     * Performs a Binary search on a list trying to find {@code v} using {@code f}. List must be ordered by {@code f}.
     *
     * @param list list to search
     * @param v    target value
     * @param f    function
     * @param <T>  list type
     * @return the index of the search key, if it is contained in the list; otherwise,
     *         {@code (-(<i>insertion point</i>) - 1)}. The <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the list: the index of the first element greater than the key, or
     *         {@code list.size()} if all elements in the list are less than the specified key. Note that this
     *         guarantees that the return value will be &gt;= 0 if and only if the key is found.
     */
    private static <T> int binarySearch(List<T> list, double v, ToDoubleFunction<T> f) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midVal = f.applyAsDouble(list.get(mid));
            int cmp = Double.compare(midVal, v);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found
    }

    @Override
    public boolean remove(Object o) {
        return portfolios.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return portfolios.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Portfolio> c) {
        boolean changed = false;
        for (Portfolio portfolio : c) {
            if (add(portfolio)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return portfolios.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
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

        MeanVarianceSet that = (MeanVarianceSet) o;

        return portfolios.equals(that.portfolios);
    }

    @Override
    public int hashCode() {
        return portfolios.hashCode();
    }

    @Override
    public Spliterator<Portfolio> spliterator() {
        return portfolios.spliterator();
    }

    /**
     * Gets a Collector for use with {@link java.util.stream.Stream#collect(Collector)}.
     *
     * @return collector which collects into a MeanVarianceSet
     */
    public static Collector<Portfolio, ?, MeanVarianceSet> collector() {
        return new MeanVarianceSetCollector();
    }
}

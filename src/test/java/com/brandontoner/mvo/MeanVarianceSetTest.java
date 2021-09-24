package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MeanVarianceSetTest {
    private final MeanVarianceSet set = new MeanVarianceSet();

    @Test
    void add_equalMean_equalVariance() {
        Portfolio p1 = createPortfolio(0, 0);
        Portfolio p2 = createPortfolio(0, 0);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
        assertContents(set, p2);
    }

    @Test
    void add_equalMean_higherVariance() {
        Portfolio p1 = createPortfolio(0, 0);
        Portfolio p2 = createPortfolio(0, 1);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_higherMean_equalVariance() {
        Portfolio p1 = createPortfolio(0, 0);
        Portfolio p2 = createPortfolio(1, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_higherVariance() {
        Portfolio p1 = createPortfolio(0, 0);
        Portfolio p2 = createPortfolio(1, 1);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p1, p2);
    }

    @Test
    void add_equalMean_lowerVariance() {
        Portfolio p1 = createPortfolio(0, 1);
        Portfolio p2 = createPortfolio(0, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_lowerVariance() {
        Portfolio p1 = createPortfolio(0, 1);
        Portfolio p2 = createPortfolio(1, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_lowerVarianceThanMultiple() {
        for (int i = 0; i < 10; ++i) {
            assertTrue(set.add(createPortfolio(i, i)));
        }
        Portfolio p1 = createPortfolio(10, 0);
        assertTrue(set.add(p1));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_equalVariance() {
        Portfolio p1 = createPortfolio(1, 0);
        Portfolio p2 = createPortfolio(0, 0);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_higherVariance() {
        Portfolio p1 = createPortfolio(1, 0);
        Portfolio p2 = createPortfolio(0, 1);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_lowerVariance() {
        Portfolio p1 = createPortfolio(1, 1);
        Portfolio p2 = createPortfolio(0, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2, p1);
    }

    private void assertContents(MeanVarianceSet set, Portfolio... expected) {
        assertEquals(List.of(expected), new ArrayList<>(set));

        List<Portfolio> splitorated = new ArrayList<>();
        set.spliterator().forEachRemaining(splitorated::add);
        assertEquals(List.of(expected), splitorated);

        List<Portfolio> iterated = new ArrayList<>();
        set.iterator().forEachRemaining(iterated::add);
        assertEquals(List.of(expected), iterated);

        assertArrayEquals(expected, set.toArray());
        assertArrayEquals(expected, set.toArray(new Portfolio[0]));
        assertArrayEquals(expected, set.toArray(Portfolio[]::new));

        for (Portfolio portfolio : expected) {
            assertTrue(set.contains(portfolio));
            assertTrue(set.containsAll(List.of(portfolio)));
        }

        assertTrue(set.containsAll(List.of(expected)));

        assertEquals(set, new MeanVarianceSet(List.of(expected)));
        assertEquals(set.hashCode(), new MeanVarianceSet(List.of(expected)).hashCode());
    }

    private static Portfolio createPortfolio(int i, int i1) {
        return new Portfolio(new Ticker[0], new int[0], i, i1);
    }
}
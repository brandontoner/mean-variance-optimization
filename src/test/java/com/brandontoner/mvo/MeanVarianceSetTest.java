package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MeanVarianceSetTest {
    private final MeanVarianceSet<PortfolioImpl> set = new MeanVarianceSet<>();

    @Test
    void add_equalMean_equalVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 0);
        PortfolioImpl p2 = new PortfolioImpl(0, 0);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
        assertContents(set, p2);
    }

    @Test
    void add_equalMean_higherVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 0);
        PortfolioImpl p2 = new PortfolioImpl(0, 1);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_higherMean_equalVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 0);
        PortfolioImpl p2 = new PortfolioImpl(1, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_higherVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 0);
        PortfolioImpl p2 = new PortfolioImpl(1, 1);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p1, p2);
    }

    @Test
    void add_equalMean_lowerVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 1);
        PortfolioImpl p2 = new PortfolioImpl(0, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_lowerVariance() {
        PortfolioImpl p1 = new PortfolioImpl(0, 1);
        PortfolioImpl p2 = new PortfolioImpl(1, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2);
    }

    @Test
    void add_higherMean_lowerVarianceThanMultiple() {
        for (int i = 0; i < 10; ++i) {
            assertTrue(set.add(new PortfolioImpl(i, i)));
        }
        PortfolioImpl p1 = new PortfolioImpl(10, 0);
        assertTrue(set.add(p1));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_equalVariance() {
        PortfolioImpl p1 = new PortfolioImpl(1, 0);
        PortfolioImpl p2 = new PortfolioImpl(0, 0);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_higherVariance() {
        PortfolioImpl p1 = new PortfolioImpl(1, 0);
        PortfolioImpl p2 = new PortfolioImpl(0, 1);
        assertTrue(set.add(p1));
        assertFalse(set.add(p2));
        assertContents(set, p1);
    }

    @Test
    void add_lowerMean_lowerVariance() {
        PortfolioImpl p1 = new PortfolioImpl(1, 1);
        PortfolioImpl p2 = new PortfolioImpl(0, 0);
        assertTrue(set.add(p1));
        assertTrue(set.add(p2));
        assertContents(set, p2, p1);
    }

    private void assertContents(MeanVarianceSet<PortfolioImpl> set, Portfolio... expected) {
        assertEquals(List.of(expected), new ArrayList<>(set));

        List<PortfolioImpl> splitorated = new ArrayList<>();
        set.spliterator().forEachRemaining(splitorated::add);
        assertEquals(List.of(expected), splitorated);

        List<PortfolioImpl> iterated = new ArrayList<>();
        set.iterator().forEachRemaining(iterated::add);
        assertEquals(List.of(expected), iterated);

        assertArrayEquals(expected, set.toArray());
        assertArrayEquals(expected, set.toArray(new PortfolioImpl[0]));
        assertArrayEquals(expected, set.toArray(PortfolioImpl[]::new));

        for (Portfolio portfolio : expected) {
            assertTrue(set.contains(portfolio));
            assertTrue(set.containsAll(List.of(portfolio)));
        }

        assertTrue(set.containsAll(List.of(expected)));

        assertEquals(set, new MeanVarianceSet<>(List.of(expected)));
        assertEquals(set.hashCode(), new MeanVarianceSet<>(List.of(expected)).hashCode());
    }

}
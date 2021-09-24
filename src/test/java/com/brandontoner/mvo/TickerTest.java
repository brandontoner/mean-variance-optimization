package com.brandontoner.mvo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

class TickerTest {
    @Test
    void constructorPersistence() {
        String name = UUID.randomUUID().toString();

        SortedMap<LocalDate, Double> map = new TreeMap<>();
        map.put(LocalDate.now(), ThreadLocalRandom.current().nextDouble());

        Ticker ticker = new Ticker(name, map);
        assertEquals(name, ticker.getName());
        assertEquals(map, ticker.getClosingPrices());
    }

    @Test
    void getClosingPrices_immutable() {
        String name = UUID.randomUUID().toString();
        SortedMap<LocalDate, Double> map = new TreeMap<>();

        Ticker ticker = new Ticker(name, map);
        assertThrows(RuntimeException.class, () -> ticker.getClosingPrices().clear());

        map.put(LocalDate.now(), Math.PI);
        assertEquals(new TreeMap<>(), ticker.getClosingPrices());
    }
}

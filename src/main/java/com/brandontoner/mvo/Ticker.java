package com.brandontoner.mvo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Ticker {
    /** Ticker name. */
    private final String name;
    /** Map of date to closing price for that day. */
    private final SortedMap<LocalDate, Double> closingPrices;
    /** Closing price per day. */
    private final double[] closingPricesArray;

    /**
     * Constructor.
     *
     * @param name          ticker name
     * @param closingPrices map of date to closing price
     */
    public Ticker(String name, Map<LocalDate, Double> closingPrices) {
        this.name = name;
        this.closingPrices = Collections.unmodifiableSortedMap(new TreeMap<>(closingPrices));
        this.closingPricesArray = this.closingPrices.values().stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Gets the ticker name.
     *
     * @return ticker name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a map of date to closing price for that day.
     *
     * @return map of date to closing price for that day
     */
    public SortedMap<LocalDate, Double> getClosingPrices() {
        return closingPrices;
    }

    /**
     * Gets the array of closing prices per day.
     *
     * @return array of closing prices per day
     */
    public double[] getClosingPricesArray() {
        return closingPricesArray;
    }
}

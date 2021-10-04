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
    /** Array of closing prices multiplied by the index. */
    private final double[][] closingPricesArrayCache;

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
        closingPricesArrayCache = new double[1000][];
        for (int i = 0; i < closingPricesArrayCache.length; ++i) {
            closingPricesArrayCache[i] = new double[closingPricesArray.length];
            for (int j = 0; j < closingPricesArray.length; j++) {
                closingPricesArrayCache[i][j] = closingPricesArray[j] * i;
            }
        }
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

    /**
     * Gets the closing prices multiplied by a coefficient.
     *
     * @param coef value to multiply the closing prices by
     */
    public double[] getClosingPricesArray(int coef) {
        return closingPricesArrayCache[coef];
    }
}

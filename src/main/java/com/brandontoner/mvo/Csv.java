package com.brandontoner.mvo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Csv {

    /**
     * Writes the portfolios as a CSV.
     *
     * @param portfolios portfolios
     * @param appendable output
     * @throws IOException on error writing
     */
    public static void writeCsv(Collection<Portfolio> portfolios, Appendable appendable) throws IOException {
        SortedSet<String> allTickers = portfolios.stream()
                                                 .map(Portfolio::countsMap)
                                                 .map(Map::entrySet)
                                                 .flatMap(Collection::stream)
                                                 .filter(e -> e.getValue() != null && e.getValue() != 0)
                                                 .map(Map.Entry::getKey)
                                                 .collect(Collectors.toCollection(TreeSet::new));
        List<Portfolio> portfolioList = new ArrayList<>(portfolios);
        portfolioList.sort(Comparator.comparingDouble(Portfolio::mean).thenComparingDouble(Portfolio::variance));

        List<String> header = new ArrayList<>(allTickers);
        header.add("mean");
        header.add("variance");
        writeCsvLine(header, appendable);

        for (Portfolio portfolio : portfolioList) {
            List<String> values = new ArrayList<>();
            for (String ticker : allTickers) {
                values.add(portfolio.countsMap().getOrDefault(ticker, 0).toString());
            }
            values.add(String.valueOf(portfolio.mean()));
            values.add(String.valueOf(portfolio.variance()));
            writeCsvLine(values, appendable);
        }
    }

    private static void writeCsvLine(Iterable<? extends CharSequence> values, Appendable a) throws IOException {
        boolean isFirst = true;
        for (CharSequence value : values) {
            if (!isFirst) {
                a.append(',');
            }
            a.append('"').append(value).append('"');
            isFirst = false;
        }
        a.append(System.lineSeparator());
    }
}

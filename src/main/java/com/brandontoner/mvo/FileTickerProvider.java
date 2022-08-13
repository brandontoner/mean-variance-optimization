package com.brandontoner.mvo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class FileTickerProvider implements Provider<List<Ticker>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LocalDate HORIZON = LocalDate.now().minusYears(1);
    private static final Pattern PATTERN = Pattern.compile("([A-Z]+)\\.csv");
    private final Path path;

    @Inject
    FileTickerProvider(@Named("csvPath") Path path) {
        this.path = path;
    }

    private static Ticker handleResponse(String ticker, String body) {
        List<String> lines = Arrays.asList(body.split("\\R"));
        SortedMap<LocalDate, Double> dateToClosingPrice = new TreeMap<>();
        for (String line : lines.subList(1, lines.size())) {
            if (line.contains("null")) {
                continue;
            }
            String[] parts = line.split(",");
            LocalDate localDate = LocalDate.parse(parts[0]);
            if (localDate.isBefore(HORIZON)) {
                continue;
            }
            dateToClosingPrice.put(localDate, Double.parseDouble(parts[4]));
        }
        return new Ticker(ticker, dateToClosingPrice);
    }

    /**
     * Reduces the set of dates for each ticker to the intersection.
     *
     * @param tickers input tickers
     * @return list of tickers with modified values
     */
    private static List<Ticker> reduceDateSet(Iterable<? extends Ticker> tickers) {
        Set<LocalDate> dates = new TreeSet<>();
        for (Ticker ticker : tickers) {
            dates.addAll(ticker.getClosingPrices().keySet());
        }
        for (Ticker ticker : tickers) {
            dates.retainAll(ticker.getClosingPrices().keySet());
        }
        List<Ticker> output = new ArrayList<>();
        for (Ticker ticker : tickers) {
            SortedMap<LocalDate, Double> closingPrices = new TreeMap<>(ticker.getClosingPrices());
            closingPrices.keySet().retainAll(dates);
            output.add(new Ticker(ticker.getName(), closingPrices));
        }
        output.sort(Comparator.comparing(Ticker::getName));
        return output;
    }

    protected String getTickerAsString(Path object) {
        LOGGER.info("Loading file {}", object);
        try {
            return Files.readString(object, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Ticker> get() {
        LOGGER.info("Listing contents of {}", path);
        try {
            try (Stream<Path> list = Files.list(path)) {
                List<Ticker> output = list.filter(Files::isRegularFile).map(file -> {
                    Matcher matcher = PATTERN.matcher(file.getFileName().toString());
                    if (!matcher.matches()) {
                        return null;
                    }
                    String ticker = matcher.group(1);
                    String string = getTickerAsString(file);
                    return FileTickerProvider.handleResponse(ticker, string);
                }).filter(Objects::nonNull).collect(Collectors.toList());
                return FileTickerProvider.reduceDateSet(output);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

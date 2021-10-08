package com.brandontoner.mvo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

@Singleton
public class S3TickerProvider implements Provider<List<Ticker>> {
    private static final Logger LOGGER = Logger.getLogger(S3TickerProvider.class.getName());
    private static final LocalDate HORIZON = LocalDate.now().minusYears(1);
    private static final Pattern PATTERN = Pattern.compile("([A-Z]+)\\.csv");
    private final String bucketName;
    private final S3Client s3Client;

    @Inject
    S3TickerProvider(@Named("tickerBucket") String bucketName, S3Client s3Client) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    @Override
    public List<Ticker> get() {
        List<Ticker> output = new ArrayList<>();
        LOGGER.info("Listing contents of " + bucketName);
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();
        for (S3Object object : s3Client.listObjectsV2Paginator(listObjectsV2Request).contents()) {
            Matcher matcher = PATTERN.matcher(object.key());
            if (!matcher.matches()) {
                continue;
            }
            LOGGER.info("Getting object s3://" + bucketName + "/" + object.key());
            String ticker = matcher.group(1);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(object.key()).build();
            try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
                byte[] bytes = response.readAllBytes();
                String string = new String(bytes, StandardCharsets.UTF_8);
                output.add(handleResponse(ticker, string));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return reduceDateSet(output);
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
        Set<LocalDate> dates = new HashSet<>();
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
}

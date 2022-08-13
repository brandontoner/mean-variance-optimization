package com.brandontoner.mvo;

import com.google.inject.Guice;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class Optimizer {
    public static final int STOCK_COUNT = 1000;
    public static final int PER_ITER = Runtime.getRuntime().availableProcessors() * 1000;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Ticker[] tickers;
    private final IonSerializer ionSerializer;

    @Inject
    public Optimizer(List<Ticker> tickers, IonSerializer ionSerializer) {
        this.tickers = tickers.toArray(Ticker[]::new);
        this.ionSerializer = ionSerializer;
    }

    /**
     * Process entry point.
     *
     * @param args ignored
     */
    public static void main(final String[] args) {
        try {
            Optimizer optimizer = Guice.createInjector(new MvoModule()).getInstance(Optimizer.class);
            optimizer.run();
        } catch (Throwable r) {
            r.printStackTrace();
            System.exit(1);
        }
    }

    private void run() throws IOException {
        getEfficientFrontier(tickers, STOCK_COUNT);
    }

    private void getEfficientFrontier(Ticker[] tickers, int n) throws IOException {
        MeanVarianceSet portfolios = ionSerializer.load(tickers);
        if (portfolios.isEmpty()) {
            portfolios.add(getEvenDistribution(tickers, n));
        }
        int lastSize = 0;
        for (int i = 0, consecutiveNoChanges = 0; consecutiveNoChanges < 5; ++i) {
            MeanVarianceSet localPortfolios = IntStream.range(0, PER_ITER)
                                                       .parallel()
                                                       .map(ignored -> ThreadLocalRandom.current()
                                                                                        .nextInt(portfolios.size()))
                                                       .mapToObj(portfolios::get)
                                                       .map(Optimizer::twiddle)
                                                       .flatMap(Collection::stream)
                                                       .collect(MeanVarianceSet.collector());
            int changed = 0;
            for (Portfolio localPortfolio : localPortfolios) {
                if (portfolios.add(localPortfolio)) {
                    changed++;
                }
            }
            if (changed == 0) {
                consecutiveNoChanges++;
            } else {
                consecutiveNoChanges = 0;
            }
            if (i % 100 == 0) {
                LOGGER.info(
                        "Iteration: {}, portfolio count: {}, (change: {}),"
                        + " changes this iteration: {}, consecutive no changes: {}",
                        i + 1,
                        portfolios.size(),
                        portfolios.size() - lastSize,
                        changed,
                        consecutiveNoChanges);
                lastSize = portfolios.size();
                ionSerializer.writeTo(portfolios);
            }
        }

        MeanVarianceSet toIterate = portfolios;
        LOGGER.info("Endgame: iterating all permutations");
        for (int i = 0; true; ++i) {
            MeanVarianceSet all = toIterate.parallelStream()
                                           .flatMap(Optimizer::allPermutations)
                                           .collect(MeanVarianceSet.collector());
            List<Portfolio> changed = new ArrayList<>();
            // TODO use portfolios.addAll()
            for (Portfolio localPortfolio : all) {
                if (portfolios.add(localPortfolio)) {
                    changed.add(localPortfolio);
                }
            }
            ionSerializer.writeTo(portfolios);
            // Only process the ones that are newly added all the existing one's
            // permutations are either worse or in changed
            toIterate = new MeanVarianceSet(changed);
            LOGGER.info("Iteration: {}, portfolio count: {}, (change: {}), changes this iteration: {}",
                        i + 1,
                        portfolios.size(),
                        portfolios.size() - lastSize,
                        changed.size());
            lastSize = portfolios.size();
            if (changed.isEmpty()) {
                break;
            }
        }

        try (PrintWriter pw = new PrintWriter(new File("out", "ef.csv"))) {
            Csv.writeCsv(portfolios, pw);
        }
    }

    private static Stream<Portfolio> allPermutations(Portfolio portfolio) {
        double[] meanAndVariance = new double[2];
        Ticker[] tickers = portfolio.tickers();
        int[] counts = portfolio.counts().clone();
        MeanVarianceSet output = new MeanVarianceSet();
        for (int i = 0; i < tickers.length; ++i) {
            final int starti = counts[i];
            for (int j = 0; j < tickers.length; ++j) {
                if (i == j) {
                    continue;
                }
                int startj = counts[j];
                while (counts[i] > 0) {
                    counts[i]--;
                    counts[j]++;

                    PortfolioFactory.getMeanAndVariance(tickers, counts, meanAndVariance);

                    output.add(tickers, counts, meanAndVariance[0], meanAndVariance[1]);
                }
                counts[i] = starti;
                counts[j] = startj;
            }
        }
        return output.stream();
    }

    private static MeanVarianceSet twiddle(Portfolio portfolio) {
        int[] counts = portfolio.counts();
        Ticker[] tickers = portfolio.tickers();
        int index1;
        int index2;
        ThreadLocalRandom current = ThreadLocalRandom.current();
        do {
            index1 = current.nextInt(counts.length);
        } while (counts[index1] == 0);
        do {
            index2 = current.nextInt(counts.length);
        } while (index2 == index1);
        MeanVarianceSet portfolios = new MeanVarianceSet();
        for (int i = 1; true; ++i) {
            if (counts[index1] - i < 0) {
                break;
            }
            int[] countsCopy = counts.clone();
            countsCopy[index1] -= i;
            countsCopy[index2] += i;
            if (!portfolios.add(PortfolioFactory.get(tickers, countsCopy))) {
                break;
            }
        }
        return portfolios;
    }

    private static Portfolio getEvenDistribution(Ticker[] tickers, int n) {
        int[] counts = new int[tickers.length];
        for (int i = 0; i < n; i++) {
            counts[i % counts.length]++;
        }
        return PortfolioFactory.get(tickers, counts);
    }

}

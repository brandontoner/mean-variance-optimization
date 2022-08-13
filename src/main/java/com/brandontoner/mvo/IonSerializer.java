package com.brandontoner.mvo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;

@Singleton
public class IonSerializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IonSystem ionSystem;
    private final Path outputDir;
    private final Path path;

    @Inject
    IonSerializer(IonSystem ionSystem, @Named("outputDir") Path outputDir) {
        this.ionSystem = ionSystem;
        this.outputDir = outputDir;
        path = outputDir.resolve("cache.ion");
    }

    /**
     * Writes portfolios to disk.
     *
     * @param portfolios iterable of portfolios to write
     * @throws IOException on error writing
     */
    public void writeTo(Iterable<Portfolio> portfolios) throws IOException {
        long startTimeNs = System.nanoTime();
        Path temp = Files.createTempFile(outputDir, "cache.", ".ion.temp");
        int written = 0;
        try (OutputStream outputStream = Files.newOutputStream(temp);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             IonWriter ionWriter = ionSystem.newBinaryWriter(bufferedOutputStream)) {
            for (Portfolio portfolio : portfolios) {
                ionWriter.stepIn(IonType.STRUCT);

                ionWriter.setFieldName("mean");
                ionWriter.writeFloat(portfolio.mean());

                ionWriter.setFieldName("variance");
                ionWriter.writeFloat(portfolio.variance());

                ionWriter.setFieldName("counts");
                ionWriter.stepIn(IonType.STRUCT);
                Ticker[] tickers = portfolio.tickers();
                int[] counts = portfolio.counts();
                for (int i = 0; i < tickers.length; i++) {
                    if (counts[i] != 0) {
                        ionWriter.setFieldName(tickers[i].getName());
                        ionWriter.writeInt(counts[i]);
                    }
                }
                ionWriter.stepOut();

                ionWriter.stepOut();
                written++;
            }
        }
        Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        long endTimeNs = System.nanoTime();
        LOGGER.info("Wrote {} entries ({} bytes) to {} in {} seconds",
                    written,
                    Files.size(path),
                    path,
                    (endTimeNs - startTimeNs) * 1.0 / TimeUnit.SECONDS.toNanos(1));
    }

    /**
     * Reads portfolios from disk.
     *
     * @param tickers array of tickers sorted by name
     * @return mean variance set
     */
    public MeanVarianceSet load(Ticker[] tickers) {
        String[] tickerNames = Arrays.stream(tickers).map(Ticker::getName).toArray(String[]::new);
        long startTimeNs = System.nanoTime();
        MeanVarianceSet output = new MeanVarianceSet();
        int read = 0;
        try (InputStream inputStream = Files.newInputStream(path);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             IonReader reader = ionSystem.newReader(bufferedInputStream)) {
            for (Iterator<IonValue> iterator = ionSystem.iterate(reader); iterator.hasNext(); ) {
                IonStruct portfolio = (IonStruct) iterator.next();
                int[] countArray = new int[tickers.length];

                boolean valid = true;
                for (IonValue count : (IonStruct) portfolio.get("counts")) {
                    int index = Arrays.binarySearch(tickerNames, count.getFieldName());
                    if (index < 0) {
                        valid = false;
                        break;
                    }
                    countArray[index] += ((IonInt) count).intValue();
                }

                if (valid) {
                    output.add(PortfolioFactory.get(tickers, countArray));
                }
                read++;

            }
            Files.copy(path, outputDir.resolve("cache." + path.toFile().lastModified() + ".ion"));
        } catch (Exception e) {
            LOGGER.error("Failed to load cache file", e);
            return output;
        }
        long endTimeNs = System.nanoTime();
        LOGGER.info("Read {} entries, {} valid ({} bytes) from {} in {} seconds",
                    read,
                    output.size(),
                    path.toFile().length(),
                    path,
                    (endTimeNs - startTimeNs) * 1.0 / TimeUnit.SECONDS.toNanos(1));
        return output;
    }
}

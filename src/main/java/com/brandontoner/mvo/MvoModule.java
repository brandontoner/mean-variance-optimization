package com.brandontoner.mvo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import software.amazon.ion.IonSystem;
import software.amazon.ion.system.IonSystemBuilder;

public class MvoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IonSystem.class).toInstance(IonSystemBuilder.standard().build());
        bind(new TypeLiteral<List<Ticker>>(){}).toProvider(FileTickerProvider.class);
    }

    @Provides
    @Singleton
    @Named("outputDir")
    Path outputDir() throws IOException {
        Path output = Path.of("out");
        Files.createDirectories(output);
        return output;
    }

    @Provides
    @Singleton
    @Named("csvPath")
    Path csvDir() {
        return Path.of("D:\\New folder (2)");
    }
}

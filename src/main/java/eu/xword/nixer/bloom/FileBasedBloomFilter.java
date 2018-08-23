package eu.xword.nixer.bloom;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.GuavaBloomFilter;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class FileBasedBloomFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();


    public static <T> BloomFilter<T> create(@Nonnull final Path filename, @Nonnull final Funnel<? super T> funnel, long expectedInsertions, double falsePositivesProbability) {
        Preconditions.checkNotNull(filename, "filename");
        Preconditions.checkNotNull(funnel, "funnel");

        final BitArray.Factory factory = BitArrayFactories.mappedFile(getDataFilePath(filename), ByteOrder.nativeOrder());
        final GuavaBloomFilter<T> filter = GuavaBloomFilter.create(funnel, expectedInsertions, falsePositivesProbability, factory);

        try {
            MAPPER.writeValue(filename.toFile(), filter.getParameters());
        } catch (IOException e) {
            // TODO: better exception class
            throw new IllegalStateException("Failed to save metadata for " + filename, e);
        }

        return filter;
    }

    public static <T> BloomFilter<T> open(Path filename, Funnel<? super T> funnel) {
        final BloomFilterParameters parameters;
        try {
            parameters = MAPPER.readValue(filename.toFile(), BloomFilterParameters.class);
        } catch (IOException e) {
            // TODO: better exception class
            throw new IllegalStateException("Failed to read metadata from " + filename, e);
        }

        final BitArray.Factory factory = BitArrayFactories.mappedFile(getDataFilePath(filename), parameters.getByteOrder());
        return GuavaBloomFilter.create(funnel, factory, parameters);
    }

    @Nonnull
    public static Path getDataFilePath(@Nonnull final Path mainPath) {
        return mainPath.resolveSibling(mainPath.getFileName() + "-data");
    }

}

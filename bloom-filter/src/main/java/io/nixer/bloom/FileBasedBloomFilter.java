package io.nixer.bloom;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import io.nixer.fork.com.google.common.hash.GuavaBloomFilter;
import io.nixer.fork.com.google.common.hash.GuavaBloomFilter;

/**
 * Offers methods to create and open file-based {@link BloomFilter}.
 *
 * <p> The filter is stored two files, one containing metadata (JSON format) and it is not changing, the second (with suffix "-data"),
 * contains actual filter content, and it can change whenever new elements are {@link BloomFilter#put(Object) put} into the filter.
 *
 * <p>The filter content is automatically saved to files by the operating system, but currently there is no control when it happens,
 * and there is no guarantee that the filter stays consistent any problems (e.g. the process is terminated or the computer reboots).
 *
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class FileBasedBloomFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Creates and opens a new file-based {@link BloomFilter}. The created data files for the filter is automatically sized
     * appropriately to handle provided number of expected insertions while probability of false positives will at specified level.
     *
     * @param filename a name of the file, where the filter metadata are stored, an additional file with suffix "-data" will be created.
     *
     * @param funnel a mechanism to turn Java objects to data used for generating hashes.
     *
     * @param expectedInsertions how many elements can be inserted in the filter while still maintaining requested probability of false positives,
     *                           note: an unlimited amount of elements can be added to the bloom filter, but if it is more than this number,
     *                           the probability of false positivies will raise dramatically
     *
     * @param falsePositivesProbability value between (0, 1) exclusive, telling what should be the upper bound of probability of false positives,
     *                                  after inserting no more than {@code expectedInsertions} elements
     * @param <T> type of elements to be handled by the filter
     * @return a filter ready to be used
     */
    @Nonnull
    public static <T> BloomFilter<T> create(
            @Nonnull final Path filename,
            @Nonnull final Funnel<? super T> funnel,
            long expectedInsertions,
            double falsePositivesProbability
    ) {
        Preconditions.checkNotNull(filename, "filename");
        Preconditions.checkNotNull(funnel, "funnel");

        Preconditions.checkArgument(
                expectedInsertions > 0,
                "expectedInsertions=%s must be bigger than zero", expectedInsertions
        );
        Preconditions.checkArgument(
                falsePositivesProbability > 0,
                "falsePositivesProbability=%s must be bigger than zero", falsePositivesProbability
        );
        Preconditions.checkArgument(
                falsePositivesProbability < 1.0,
                "falsePositivesProbability=%s must be less than 1.0", falsePositivesProbability
        );

        final BitArray.Factory factory = BitArrayFactories.mappedFile(getDataFilePath(filename), ByteOrder.nativeOrder());
        final GuavaBloomFilter<T> filter = GuavaBloomFilter.create(funnel, expectedInsertions, falsePositivesProbability, factory);

        try {
            MAPPER.writeValue(filename.toFile(), filter.getParameters());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save metadata for " + filename, e);
        }

        return filter;
    }

    /**
     * Opens an existing file-based filter, created by {@link #create(Path, Funnel, long, double)}.
     *
     * @param filename a name of the file, where the filter metadata are stored
     * @param funnel a mechanism to turn Java objects to data used for generating hashes.
     * @param <T> type of elements to be handled by the filter
     * @return a filter ready to be used
     */
    @Nonnull
    public static <T> BloomFilter<T> open(@Nonnull final Path filename, @Nonnull final Funnel<? super T> funnel) {
        Preconditions.checkNotNull(filename, "filename");
        Preconditions.checkNotNull(funnel, "funnel");

        final BloomFilterParameters parameters;
        try {
            parameters = MAPPER.readValue(filename.toFile(), BloomFilterParameters.class);
        } catch (IOException e) {
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

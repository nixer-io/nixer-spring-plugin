package eu.xword.nixer.bloom;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnels;

/**
 * Convenience wrapper over a file-based Bloom filter allowing to use the filter as a String predicate.
 * It uses {@link HashingStrategy} for converting input values to bytes before checking them against the wrapped Bloom filter.
 *
 * <br>
 * Created on 07/10/2019.
 *
 * @author gcwiak
 */
public class BloomFilterCheck implements Predicate<String> {

    private final BloomFilter<byte[]> bloomFilter;

    private final HashingStrategy hashingStrategy;

    private BloomFilterCheck(final Path filename, final HashingStrategy hashingStrategy) {
        Preconditions.checkNotNull(filename, "filename");
        Preconditions.checkNotNull(hashingStrategy, "hashingStrategy");

        this.bloomFilter = openBloomFilter(filename);
        this.hashingStrategy = hashingStrategy;
    }

    private static BloomFilter<byte[]> openBloomFilter(final Path filename) {
        return FileBasedBloomFilter.open(
                filename,
                Funnels.byteArrayFunnel()
        );
    }

    @Override
    public boolean test(final String value) {
        final byte[] valueBytes = hashingStrategy.convertToBytes(value);

        return bloomFilter.mightContain(valueBytes);
    }

    public static BloomFilterCheck hashingBeforeCheck(final Path filename) {
        return new BloomFilterCheck(filename, new Sha1Hashing());
    }

    public static BloomFilterCheck notHashingBeforeCheck(final Path filename) {
        return new BloomFilterCheck(filename, new NoHashing());
    }

}

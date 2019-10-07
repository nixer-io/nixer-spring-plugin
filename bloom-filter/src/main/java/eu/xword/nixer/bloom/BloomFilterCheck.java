package eu.xword.nixer.bloom;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created on 07/10/2019.
 *
 * @author gcwiak
 */
public abstract class BloomFilterCheck implements Predicate<String> {

    private final BloomFilter<byte[]> bloomFilter;

    // TODO javadoc
    // FIXME refactor to non-anonymous classes
    public static BloomFilterCheck hashingBeforeCheck(final Path filename) {

        final HashFunction hashFunction = Hashing.sha1();  // TODO consider externalizing

        return new BloomFilterCheck(openBloomFilter(filename)) {
            @Override
            protected byte[] convertToBytes(final String value) {
                final byte[] valueBytes = value.getBytes(Charsets.UTF_8);
                return hashFunction.hashBytes(valueBytes).asBytes();
            }
        };
    }

    public static BloomFilterCheck notHashingBeforeCheck(final Path filename) {
        return new BloomFilterCheck(openBloomFilter(filename)) {
            @Override
            protected byte[] convertToBytes(final String value) {
                return HashCode.fromString(value.toLowerCase()).asBytes();
            }
        };
    }

    private BloomFilterCheck(final BloomFilter<byte[]> bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    @Override
    public boolean test(final String value) {

        final byte[] valueBytes = convertToBytes(value);

        return bloomFilter.mightContain(valueBytes);
    }

    protected abstract byte[] convertToBytes(final String value);

    private static BloomFilter<byte[]> openBloomFilter(final Path filename) {
        return FileBasedBloomFilter.open(
                filename,
                Funnels.byteArrayFunnel()
        );
    }
}

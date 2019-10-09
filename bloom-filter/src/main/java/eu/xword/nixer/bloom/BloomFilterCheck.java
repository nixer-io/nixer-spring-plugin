package eu.xword.nixer.bloom;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * Convenience wrapper over a file-based bloom filter allowing to use the filter as a String predicate.
 * Comes in two variants, {@link NotHashingInputs} that verifies the passed value is hex string before the check on filter
 * and {@link HashingInputs} which computes SHA-1 hash of the passed value before doing the check.
 *
 * <br>
 * Created on 07/10/2019.
 *
 * @author gcwiak
 */
public abstract class BloomFilterCheck implements Predicate<String> {

    private final BloomFilter<byte[]> bloomFilter;

    private BloomFilterCheck(final Path filename) {
        this.bloomFilter = openBloomFilter(filename);
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

    public static BloomFilterCheck hashingBeforeCheck(final Path filename) {
        return new HashingInputs(filename);
    }

    public static BloomFilterCheck notHashingBeforeCheck(final Path filename) {
        return new NotHashingInputs(filename);
    }

    /**
     * Implementations:
     */

    private static class HashingInputs extends BloomFilterCheck {

        private final HashFunction hashFunction = Hashing.sha1();  // TODO consider externalizing

        HashingInputs(final Path filename) {
            super(filename);
        }

        @Override
        protected byte[] convertToBytes(final String value) {
            final byte[] valueBytes = value.getBytes(Charsets.UTF_8);
            return hashFunction.hashBytes(valueBytes).asBytes();
        }
    }

    private static class NotHashingInputs extends BloomFilterCheck {
        NotHashingInputs(final Path filename) {
            super(filename);
        }

        @Override
        protected byte[] convertToBytes(final String value) {
            if (BaseEncoding.base16().canDecode(value)) {
                return HashCode.fromString(value.toLowerCase()).asBytes();
            } else {
                throw new NotHexStringException(value);
            }
        }
    }
}

package eu.xword.nixer.bloom;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.file.Path;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.hash.GuavaBloomFilterStrategies;

/**
 * Offers various types of {@link BitArray.Factory BitArray factories}.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BitArrayFactories {

    /**
     * Returns a factory that creates {@link BitArray} that is completely in memory, just from the Guava.
     * Only reason to use it, if you want to write code that can be used with both in-memory and file-based bloom-filters.
     */
    public static BitArray.Factory inMemory() {
        return GuavaBloomFilterStrategies.BitArrayInMemory::new;
    }

    /**
     * Returns a factory that creates {@link BitArray} which is stored in a file system under specified path.
     *
     * @param path          a path to file with data, if it does not exist, it will be created.
     * @param byteOrder     a byte order of data in the file, if the file already exist,
     *                      it must be the same as it was set when the file was created.
     */
    public static BitArray.Factory mappedFile(@Nonnull final Path path, @Nonnull final ByteOrder byteOrder) {
        Preconditions.checkNotNull(path, "path");
        Preconditions.checkNotNull(byteOrder, "byteOrder");

        return bitSize -> buildBitArrayMappedFile(path, bitSize, byteOrder);
    }


    @Nonnull
    private static BitArray buildBitArrayMappedFile(@Nonnull final Path path, final long bitSize, @Nonnull final ByteOrder byteOrder) {

        try(final RandomAccessFile openedFile = new RandomAccessFile(path.toFile(), "rw")) {
            validateAndPotentiallyCorrectFileLength(bitSize, openedFile);
            return new BitArrayMappedFile(openedFile.getChannel(), byteOrder);
        } catch (IOException e) {
            // TODO: better exception class
            throw new IllegalStateException("Failed to open a bloom filter data because an I/O failure file=" + path, e);
        }

    }

    private static void validateAndPotentiallyCorrectFileLength(
            final long bitSize,
            @Nonnull final RandomAccessFile openedFile
    ) throws IOException {

        final long currentSize = openedFile.length();

        // rounded up to full longs
        final long numberOfLongsForData = (bitSize + 63) / 64 /* number of bits in one long*/;
        final long targetSize = (numberOfLongsForData + 1 /* extra one long to store bitCount*/) * 8 /* bytes in one long*/;

        if (currentSize != targetSize) {
            Preconditions.checkState(
                    currentSize == 0, // for zero we assume the file was just created empty
                    "Mismatch between actual and expected file size actual=%s expected=%s",
                    currentSize, targetSize
            );

            // Note: Java does not guarantee what will be content of extended file, but on any sane platform it should extended with zeros
            openedFile.setLength(targetSize);
        }
    }
}

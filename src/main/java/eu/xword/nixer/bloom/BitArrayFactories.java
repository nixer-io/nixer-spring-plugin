package eu.xword.nixer.bloom;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.hash.GuavaBloomFilterStrategies;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BitArrayFactories {
    public static BitArray.Factory inMemory() {
        return GuavaBloomFilterStrategies.BitArrayInMemory::new;
    }

    public static BitArray.Factory mappedFile(@Nonnull final Path path, @Nonnull final ByteOrder byteOrder) {
        return bitSize -> buildBitArrayMappedFile(path, bitSize, byteOrder);
    }

    @Nonnull
    private static BitArray buildBitArrayMappedFile(final Path path, final long bitSize, final ByteOrder byteOrder) {
        try(final RandomAccessFile openedFile = new RandomAccessFile(path.toFile(), "rw")) {
            validateAndPotentiallyCorrectLength(bitSize, openedFile);
            return new BitArrayMappedFile(openedFile.getChannel(), byteOrder);
        } catch (IOException e) {
            // TODO: better exception class
            throw new IllegalStateException("Failed to open a bloom filter data because an I/O failure file=" + path, e);
        }
    }

    private static void validateAndPotentiallyCorrectLength(final long bitSize, final RandomAccessFile openedFile) throws IOException {
        final long currentSize = openedFile.length();
        final long targetSize = (((bitSize + 63) / 64) + 1) * 8;

        if (currentSize != targetSize) {
            Preconditions.checkState(
                    currentSize == 0,
                    "Mismatch between actual and expected file size actual=%s expected=%s",
                    currentSize, targetSize
            );

            // Note: Java does not guarantee what will be content of extended file, but on any sane platform it should extended with zeros
            openedFile.setLength(targetSize);
        }
    }

}

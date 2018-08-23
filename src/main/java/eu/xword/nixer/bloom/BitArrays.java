package eu.xword.nixer.bloom;

import java.io.IOException;
import java.io.RandomAccessFile;
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
public class BitArrays {
    public BitArray.Factory inMemory() {
        return GuavaBloomFilterStrategies.BitArrayInMemory::new;
    }

    public BitArray.Factory mappedFile(Path path) {
        return bitSize -> buildBitArrayMappedFile(path, bitSize);
    }

    @Nonnull
    private static BitArray buildBitArrayMappedFile(final Path path, final long bitSize) {
        try(final RandomAccessFile openedFile = new RandomAccessFile(path.toFile(), "rw")) {
            validateAndPotentiallyCorrectLength(bitSize, openedFile);
            return new BitArrayMappedFile(openedFile.getChannel());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open bloom filter because an I/O failure", e);
        }
    }

    private static void validateAndPotentiallyCorrectLength(final long bitSize, final RandomAccessFile openedFile) throws IOException {
        final long currentSize = openedFile.length();
        final long targetSize = bitSize / 8;

        if (currentSize != targetSize) {
            Preconditions.checkState(
                    currentSize != 0,
                    "Mismatch between actual and expected file size actual=% expected=%s",
                    currentSize, targetSize
            );

            // Note: Java does not guarantee what will be content of extended file, but on any sane platform it should extended with zeros
            openedFile.setLength(targetSize);
        }
    }

}

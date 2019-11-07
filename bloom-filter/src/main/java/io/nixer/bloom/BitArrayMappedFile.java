package io.nixer.bloom;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

/**
 * An implementation of {@link BitArray} that stores all data in a file that is mmaped to the processes space, not loaded.
 * <p>It is designed to handle large data files of sizes many gigabytes, without requiring them to explicitly read or write from/to the disc.
 * It does not also use Java's heap space for the data storage.
 * <p>
 * Note: currently it does not offer any consistency guarantees in case of failure when data is modified. It is not multi-thread safe
 * with respect to writes, but reads can be invoked concurrently.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BitArrayMappedFile implements BitArray {

    /** Maximum size of one segment when mmapping, constrained by Java's {@link java.nio.ByteBuffer} API limitations */
    private static int SEGMENT_SIZE_POW_2 = 30; // each segment is currently 1GB
    private static int SEGMENT_SIZE_BYTES = 1 << SEGMENT_SIZE_POW_2;
    private static int SEGMENT_SIZE_MASK = (1 << SEGMENT_SIZE_POW_2) - 1;

    private final long bitSize;

    // because Java API for ByteBuffers uses int everywhere, we open multiple mappings each of maximum size of SEGMENT_SIZE_BYTES
    private final MappedByteBuffer[] mappedByteBuffers;

     /** The same as {@code mappedByteBuffers[0]} */
    private final MappedByteBuffer firstByteBuffer;

    public BitArrayMappedFile(@Nonnull final FileChannel fileChannel, @Nonnull final ByteOrder byteOrder) throws IOException {
        Preconditions.checkNotNull(fileChannel, "fileChannel");
        Preconditions.checkNotNull(byteOrder, "byteOrder");

        final long totalSizeInBytes = fileChannel.size();

        this.bitSize = totalSizeInBytes * 8 - 64; // 64 corrects for first long containing bitCount

        Preconditions.checkArgument(this.bitSize > 0, "Bloom filter data file is too small");

        final int segments = Ints.checkedCast(LongMath.divide(totalSizeInBytes, SEGMENT_SIZE_BYTES, RoundingMode.CEILING));
        assert segments > 0;

        this.mappedByteBuffers = createMappedByteBuffers(fileChannel, segments, byteOrder, totalSizeInBytes);

        firstByteBuffer = this.mappedByteBuffers[0];
    }

    @Nonnull
    private static MappedByteBuffer[] createMappedByteBuffers(
            @Nonnull final FileChannel fileChannel,
            final int segments,
            @Nonnull final ByteOrder byteOrder,
            final long totalSizeInBytes
    ) throws IOException {

        final MappedByteBuffer[] mappedByteBuffers = new MappedByteBuffer[segments];

        long remainingSize = totalSizeInBytes;
        long offset = 0;
        for (int i = 0; i < segments; i++) {
            final long size = remainingSize > SEGMENT_SIZE_BYTES
                    ? SEGMENT_SIZE_BYTES
                    : remainingSize;

            // note: memory mappings are valid even if the file is closed
            mappedByteBuffers[i] = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, size);
            mappedByteBuffers[i].order(byteOrder);
            remainingSize -= size;
            offset += size;
            assert remainingSize >= 0;
            assert offset <= totalSizeInBytes;
        }

        assert remainingSize == 0;
        assert offset == totalSizeInBytes;

        return mappedByteBuffers;
    }

    @Override
    public boolean set(final long index) {
        return !getAndMaybeSet(index, true);
    }

    @Override
    public boolean get(final long index) {
        return getAndMaybeSet(index, false);
    }

    private boolean getAndMaybeSet(final long index, final boolean alsoSet) {
        final long correctedIndex = index + 64; // corrects for existence for bitCount stored at index 0
        final long longPosition = correctedIndex >>> 6;
        final long segmentIndex = longPosition >>> (SEGMENT_SIZE_POW_2 - 3);
        final int bytPositionInSegment = Ints.checkedCast((longPosition << 3) & SEGMENT_SIZE_MASK);

        final MappedByteBuffer segment = mappedByteBuffers[Ints.checkedCast(segmentIndex)];
        final long value = segment.getLong(bytPositionInSegment);

        final long bitMask = 1L << correctedIndex;
        final boolean wasSet = (value & bitMask) != 0;

        if (wasSet || !alsoSet) {
            return wasSet;
        }

        final long changedValue = value | bitMask;
        segment.putLong(bytPositionInSegment, changedValue);
        final long newCount = firstByteBuffer.getLong(0) + 1;
        firstByteBuffer.putLong(0, newCount);

        return false;
    }

    @Override
    public long bitSize() {
        return bitSize;
    }

    @Override
    public long bitCount() {
        return firstByteBuffer.getLong(0);
    }
}

package eu.xword.nixer.bloom;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BitArrayMappedFile implements BitArray {

    /** Maximum size of one segment */
    private static int SEGMENT_SIZE_POW_2 = 30; // each segment is currently 1GB
    private static int SEGMENT_SIZE_BYTES = 1 << SEGMENT_SIZE_POW_2;
    private static int SEGMENT_SIZE_IN_LONG_POW_2 = SEGMENT_SIZE_POW_2 - 3;
    private static int SEGMENT_SIZE_IN_LONG_MASK = (1 << SEGMENT_SIZE_IN_LONG_POW_2 - 1);
    private final long bitSize;

    // because Java API for ByteBuffers uses int everywhere, we open multiple mappings each of maximum size of SEGMENT_SIZE_IN_BYTES
    private final MappedByteBuffer[] mappedByteBuffers;

    public BitArrayMappedFile(@Nonnull final FileChannel fileChannel) throws IOException {
        Preconditions.checkNotNull(fileChannel, "fileChannel");

        final long totalSizeInBytes = fileChannel.size();
        this.bitSize = totalSizeInBytes * 8;
        final long segments = (totalSizeInBytes / SEGMENT_SIZE_BYTES) + 1;
        this.mappedByteBuffers = new MappedByteBuffer[Ints.checkedCast(segments)];
        long remainingSize = totalSizeInBytes;
        for (int i = 0; i < segments; i++) {
            final long size = remainingSize > SEGMENT_SIZE_BYTES
                    ? SEGMENT_SIZE_BYTES
                    : remainingSize;
            remainingSize -= size;

            // note: memory mappings are valid even if the file is closed
            mappedByteBuffers[i] = fileChannel.map(FileChannel.MapMode.READ_WRITE, ((long) i) * SEGMENT_SIZE_BYTES, size);
        }

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
        final long indexInLongs = correctedIndex >>> 6;
        final long segmentIndex = indexInLongs >>> SEGMENT_SIZE_IN_LONG_POW_2;
        final int indexInSegmentInLongs = Ints.checkedCast(indexInLongs & SEGMENT_SIZE_IN_LONG_MASK);

        final MappedByteBuffer segment = mappedByteBuffers[Ints.checkedCast(segmentIndex)];
        final long value = segment.getLong(indexInSegmentInLongs);

        final long bitMask = 1L << correctedIndex;
        final boolean wasSet = (value & bitMask) != 0;
        if (alsoSet && !wasSet) {
            final long changedValue = value | bitMask;
            segment.putLong(indexInSegmentInLongs, changedValue);
        }
        return wasSet;
    }

    @Override
    public long bitSize() {
        return bitSize;
    }

    @Override
    public long bitCount() {
        return mappedByteBuffers[0].getLong(0);
    }
}

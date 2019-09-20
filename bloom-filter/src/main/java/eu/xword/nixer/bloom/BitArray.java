package eu.xword.nixer.bloom;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * An interface to a data structure which allows individual bits to be set.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public interface BitArray {

    /** Returns true if the bit changed value. */
    boolean set(long index);

    /** Returns true if the bit was {@link #set(long)}. */
    boolean get(long index);

    /** Number of bits */
    long bitSize();

    /** Number of set bits (1s) */
    long bitCount();

    /**
     * A factory to create {@link BitArray}. Use {@link BitArrayFactories} to get an appropriate factory.
     */
    @FunctionalInterface
    interface Factory {

        /** Creates a new {@link BitArray}. It might fail if invoked multiple times. */
        @Nonnull
        BitArray create(@Nonnegative long bitSize);
    }

}

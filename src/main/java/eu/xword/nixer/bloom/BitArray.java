package eu.xword.nixer.bloom;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public interface BitArray {

    /** Returns true if the bit changed value. */
    boolean set(long index);

    boolean get(long index);

    /** Number of bits */
    long bitSize();

    /** Number of set bits (1s) */
    long bitCount();

    @FunctionalInterface
    interface Factory {
        BitArray create(long bitSize);
    }

}

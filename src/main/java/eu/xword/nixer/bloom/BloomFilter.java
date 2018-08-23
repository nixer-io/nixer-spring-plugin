package eu.xword.nixer.bloom;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;
import com.google.common.hash.Funnel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public interface BloomFilter<T> extends Predicate<T> {
    /**
     * Returns {@code true} if the element <i>might</i> have been put in this Bloom filter,
     * {@code false} if this is <i>definitely</i> not the case.
     */
    boolean mightContain(T object);

    /**
     * Puts an element into this {@code GuavaBloomFilter}. Ensures that subsequent invocations of {@link
     * #mightContain(Object)} with the same element will always return {@code true}.
     *
     * @return true if the Bloom filter's bits changed as a result of this operation. If the bits
     *     changed, this is <i>definitely</i> the first time {@code object} has been added to the
     *     filter. If the bits haven't changed, this <i>might</i> be the first time {@code object} has
     *     been added to the filter. Note that {@code put(t)} always returns the <i>opposite</i>
     *     result to what {@code mightContain(t)} would have returned at the time it is called.
     */
    @CanIgnoreReturnValue
    boolean put(T object);

    /**
     * Returns the probability that {@linkplain #mightContain(Object)} will erroneously return
     * {@code true} for an object that has not actually been put in the {@code GuavaBloomFilter}.
     *
     * <p>Ideally, this number should be close to the {@code fpp} parameter passed in
     * {@linkplain #create(Funnel, int, double, BitArray.Factory)}, or smaller. If it is significantly higher, it is
     * usually the case that too many elements (more than expected) have been put in the
     * {@code GuavaBloomFilter}, degenerating it.
     */
    double expectedFpp();

    /**
     * Returns an estimate for the total number of distinct elements that have been added to this
     * Bloom filter. This approximation is reasonably accurate if it does not exceed the value of
     * {@code expectedInsertions} that was used when constructing the filter.
     */
    long approximateElementCount();

    @Nonnull
    BloomFilterParameters getParameters();
}

package eu.xword.nixer.bloom;

import java.nio.ByteOrder;
import java.util.Map;
import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.GuavaBloomFilter;
import com.google.common.hash.GuavaBloomFilterStrategies;

/**
 * A set of parameters describing {@link BloomFilter} when it was created.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BloomFilterParameters {
    private static final Map<String, ByteOrder> BYTE_ORDER_MAP = ImmutableMap.of(
        ByteOrder.BIG_ENDIAN.toString(), ByteOrder.BIG_ENDIAN,
        ByteOrder.LITTLE_ENDIAN.toString(), ByteOrder.LITTLE_ENDIAN
    );

    private final long expectedInsertions;
    private final double falsePositivesProbability;
    private final int numHashFunctions;
    private final long bitSize;

    @Nonnull
    private final GuavaBloomFilterStrategies strategy;

    @Nonnull
    private final ByteOrder byteOrder;


    public BloomFilterParameters(
            final long expectedInsertions,
            final double falsePositivesProbability,
            final int numHashFunctions,
            final long bitSize,
            @Nonnull final GuavaBloomFilterStrategies strategy,
            @Nonnull final ByteOrder byteOrder
    ) {
        Preconditions.checkNotNull(strategy, "strategy");
        Preconditions.checkNotNull(byteOrder, "byteOrder");

        this.expectedInsertions = expectedInsertions;
        this.falsePositivesProbability = falsePositivesProbability;
        this.numHashFunctions = numHashFunctions;
        this.bitSize = bitSize;
        this.strategy = strategy;
        this.byteOrder = byteOrder;
    }


    // Used for de-serialisation
    @JsonCreator
    public BloomFilterParameters(
            @JsonProperty("expectedInsertions") final long expectedInsertions,
            @JsonProperty("falsePositivesProbability") final double falsePositivesProbability,
            @JsonProperty("numHashFunctions") final int numHashFunctions,
            @JsonProperty("bitSize") final long bitSize,
            @JsonProperty("strategy") @Nonnull final GuavaBloomFilterStrategies strategy,
            @JsonProperty("byteOrder") @Nonnull final String byteOrder
    ) {
        Preconditions.checkNotNull(strategy, "strategy");
        Preconditions.checkNotNull(byteOrder, "byteOrder");

        this.expectedInsertions = expectedInsertions;
        this.falsePositivesProbability = falsePositivesProbability;
        this.numHashFunctions = numHashFunctions;
        this.bitSize = bitSize;
        this.strategy = strategy;
        this.byteOrder = Preconditions.checkNotNull(BYTE_ORDER_MAP.get(byteOrder), "Invalid value for byteOrder");
    }

    @JsonProperty
    public double getFalsePositivesProbability() {
        return falsePositivesProbability;
    }

    public long getExpectedInsertions() {
        return expectedInsertions;
    }

    @JsonProperty
    public int getNumHashFunctions() {
        return numHashFunctions;
    }

    @JsonProperty
    public long getBitSize() {
        return bitSize;
    }

    @JsonProperty
    @Nonnull
    public GuavaBloomFilter.Strategy getStrategy() {
        return strategy;
    }

    @JsonIgnore
    @Nonnull
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    // used for serialisation
    @JsonProperty("byteOrder")
    @Nonnull
    public String getByteOrderString() {
        return byteOrder.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BloomFilterParameters that = (BloomFilterParameters) o;

        if (expectedInsertions != that.expectedInsertions) return false;
        if (Double.compare(that.falsePositivesProbability, falsePositivesProbability) != 0) return false;
        if (numHashFunctions != that.numHashFunctions) return false;
        if (bitSize != that.bitSize) return false;
        if (!strategy.equals(that.strategy)) return false;
        return byteOrder.equals(that.byteOrder);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (expectedInsertions ^ (expectedInsertions >>> 32));
        temp = Double.doubleToLongBits(falsePositivesProbability);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + numHashFunctions;
        result = 31 * result + (int) (bitSize ^ (bitSize >>> 32));
        result = 31 * result + strategy.hashCode();
        result = 31 * result + byteOrder.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expectedInsertions", expectedInsertions)
                .add("falsePositivesProbability", falsePositivesProbability)
                .add("numHashFunctions", numHashFunctions)
                .add("bitSize", bitSize)
                .add("strategy", strategy)
                .add("byteOrder", byteOrder)
                .toString();
    }
}

package eu.xword.nixer.bloom;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.hash.GuavaBloomFilterStrategies;

/**
 * TODO
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */

public class BloomFilterParameters {
    private final long expectedInsertions;
    private final double expectedFalsePositives;
    private final int numHashFunctions;
    private final long bitSize;
    @Nonnull
    private final GuavaBloomFilterStrategies strategy;

    @JsonCreator
    public BloomFilterParameters(
            @JsonProperty("expectedInsertions") final long expectedInsertions,
            @JsonProperty("expectedFalsePositives") final double expectedFalsePositives,
            @JsonProperty("numHashFunctions") final int numHashFunctions,
            @JsonProperty("bitSize") final long bitSize,
            @JsonProperty("strategy") @Nonnull final GuavaBloomFilterStrategies strategy
    ) {
        this.expectedInsertions = expectedInsertions;
        this.expectedFalsePositives = expectedFalsePositives;
        this.numHashFunctions = numHashFunctions;
        this.bitSize = bitSize;
        this.strategy = strategy;
    }

    @JsonProperty
    public double getExpectedFalsePositives() {
        return expectedFalsePositives;
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
    public GuavaBloomFilterStrategies getStrategy() {
        return strategy;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BloomFilterParameters that = (BloomFilterParameters) o;

        if (expectedInsertions != that.expectedInsertions) return false;
        if (Double.compare(that.expectedFalsePositives, expectedFalsePositives) != 0) return false;
        if (numHashFunctions != that.numHashFunctions) return false;
        if (bitSize != that.bitSize) return false;
        return strategy == that.strategy;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (expectedInsertions ^ (expectedInsertions >>> 32));
        temp = Double.doubleToLongBits(expectedFalsePositives);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + numHashFunctions;
        result = 31 * result + (int) (bitSize ^ (bitSize >>> 32));
        result = 31 * result + strategy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expectedInsertions", expectedInsertions)
                .add("expectedFalsePositives", expectedFalsePositives)
                .add("numHashFunctions", numHashFunctions)
                .add("bitSize", bitSize)
                .add("strategy", strategy)
                .toString();
    }
}

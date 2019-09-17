/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.hash;

import java.math.RoundingMode;

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import eu.xword.nixer.bloom.BitArray;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *  Copied from Guava 22.0 (com.google.common.hash.BloomFilterStrategies)- all changes marked with "CROSSWORD CHANGES"
 *  <br>
 *  See: https://github.com/google/guava/
 *  <br>
 *  License: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Collections of strategies of generating the k * log(M) bits required for an element to be mapped
 * to a GuavaBloomFilter of M bits and k hash functions. These strategies are part of the serialized form
 * of the Bloom filters that use them, thus they must be preserved as is (no updates allowed, only
 * introduction of new versions).
 *
 * Important: the order of the constants cannot change, and they cannot be deleted - we depend on
 * their ordinal for GuavaBloomFilter serialization.
 *
 * @author Dimitris Andreou
 * @author Kurt Alfred Kluever
 */
// CROSSWORD CHANGES - class made public
public enum GuavaBloomFilterStrategies implements GuavaBloomFilter.Strategy {
  /**
   * See "Less Hashing, Same Performance: Building a Better Bloom Filter" by Adam Kirsch and Michael
   * Mitzenmacher. The paper argues that this trick doesn't significantly deteriorate the
   * performance of a Bloom filter (yet only needs two 32bit hash functions).
   */
  MURMUR128_MITZ_32() {
    @Override
    public <T> boolean put(
        T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) {
      long bitSize = bits.bitSize();
      long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
      int hash1 = (int) hash64;
      int hash2 = (int) (hash64 >>> 32);

      boolean bitsChanged = false;
      for (int i = 1; i <= numHashFunctions; i++) {
        int combinedHash = hash1 + (i * hash2);
        // Flip all the bits if it's negative (guaranteed positive number)
        if (combinedHash < 0) {
          combinedHash = ~combinedHash;
        }
        bitsChanged |= bits.set(combinedHash % bitSize);
      }
      return bitsChanged;
    }

    @Override
    public <T> boolean mightContain(
        T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) {
      long bitSize = bits.bitSize();
      long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
      int hash1 = (int) hash64;
      int hash2 = (int) (hash64 >>> 32);

      for (int i = 1; i <= numHashFunctions; i++) {
        int combinedHash = hash1 + (i * hash2);
        // Flip all the bits if it's negative (guaranteed positive number)
        if (combinedHash < 0) {
          combinedHash = ~combinedHash;
        }
        if (!bits.get(combinedHash % bitSize)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * This strategy uses all 128 bits of {@link Hashing#murmur3_128} when hashing. It looks different
   * than the implementation in MURMUR128_MITZ_32 because we're avoiding the multiplication in the
   * loop and doing a (much simpler) += hash2. We're also changing the index to a positive number by
   * AND'ing with Long.MAX_VALUE instead of flipping the bits.
   */
  MURMUR128_MITZ_64() {
    @Override
    public <T> boolean put(
        T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) {
      long bitSize = bits.bitSize();
      byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).asBytes();
      long hash1 = lowerEight(bytes);
      long hash2 = upperEight(bytes);

      boolean bitsChanged = false;
      long combinedHash = hash1;
      for (int i = 0; i < numHashFunctions; i++) {
        // Make the combined hash positive and indexable
        bitsChanged |= bits.set((combinedHash & Long.MAX_VALUE) % bitSize);
        combinedHash += hash2;
      }
      return bitsChanged;
    }

    @Override
    public <T> boolean mightContain(
        T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) {
      long bitSize = bits.bitSize();
      byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).asBytes();
      long hash1 = lowerEight(bytes);
      long hash2 = upperEight(bytes);

      long combinedHash = hash1;
      for (int i = 0; i < numHashFunctions; i++) {
        // Make the combined hash positive and indexable
        if (!bits.get((combinedHash & Long.MAX_VALUE) % bitSize)) {
          return false;
        }
        combinedHash += hash2;
      }
      return true;
    }

    private /* static */ long lowerEight(byte[] bytes) {
      return Longs.fromBytes(
          bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    private /* static */ long upperEight(byte[] bytes) {
      return Longs.fromBytes(
          bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
    }
  };

  // CROSSWORD CHANGES - originally there was no interface and this class was just non-public BitArray
  // Note: We use this instead of java.util.BitSet because we need access to the long[] data field
  public static final class BitArrayInMemory implements BitArray {
    final long[] data;
    long bitCount;

    public BitArrayInMemory(long bits) {
      this(new long[Ints.checkedCast(LongMath.divide(bits, 64, RoundingMode.CEILING))]);
    }

    // Used by serialization
    BitArrayInMemory(long[] data) {
      checkArgument(data.length > 0, "data length is zero!");
      this.data = data;
      long bitCount = 0;
      for (long value : data) {
        bitCount += Long.bitCount(value);
      }
      this.bitCount = bitCount;
    }

    // CROSSWORD CHANGES - all methods made public (from package default)

    /** Returns true if the bit changed value. */
    public boolean set(long index) {
      if (!get(index)) {
        data[(int) (index >>> 6)] |= (1L << index);
        bitCount++;
        return true;
      }
      return false;
    }

    public boolean get(long index) {
      return (data[(int) (index >>> 6)] & (1L << index)) != 0;
    }

    /** Number of bits */
    public long bitSize() {
      return (long) data.length * Long.SIZE;
    }

    /** Number of set bits (1s) */
    public long bitCount() {
      return bitCount;
    }

    //  --- CROSSWORD CHANGES - removed copy() method

    //  --- CROSSWORD CHANGES - removed putAll() method

    //  --- CROSSWORD CHANGES - removed hashCode() and equals()
  }


}

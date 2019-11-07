package io.nixer.bloom.check;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Computes SHA-1 hash of the passed value and converts it to bytes.
 *
 * Created on 10/10/2019.
 *
 * @author gcwiak
 */
class Sha1Hashing implements HashingStrategy {

    private final HashFunction sha1 = Hashing.sha1();

    @Override
    public byte[] convertToBytes(final String value) {
        final byte[] valueBytes = value.getBytes(Charsets.UTF_8);
        return sha1.hashBytes(valueBytes).asBytes();
    }
}

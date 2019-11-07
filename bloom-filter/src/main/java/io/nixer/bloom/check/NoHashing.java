package io.nixer.bloom.check;

import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import io.nixer.bloom.NotHexStringException;
import io.nixer.bloom.NotHexStringException;

/**
 *  Does not do any hashing. Assures the input value is a valid hex string and converts it to bytes.
 *
 * Created on 10/10/2019.
 *
 * @author gcwiak
 */
class NoHashing implements HashingStrategy {

    @Override
    public byte[] convertToBytes(final String value) {
        if (isHexString(value)) {
            return HashCode.fromString(value.toLowerCase()).asBytes();
        } else {
            throw new NotHexStringException(value);
        }
    }

    private boolean isHexString(final String value) {
        return !Strings.isNullOrEmpty(value) && BaseEncoding.base16().canDecode(value);
    }
}

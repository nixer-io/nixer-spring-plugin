package io.nixer.bloom.check;

/**
 * Created on 10/10/2019.
 *
 * @author gcwiak
 */
interface HashingStrategy {

    byte[] convertToBytes(final String value);

}

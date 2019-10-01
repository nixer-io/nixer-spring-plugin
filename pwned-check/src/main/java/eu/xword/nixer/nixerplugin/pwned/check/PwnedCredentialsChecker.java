package eu.xword.nixer.nixerplugin.pwned.check;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import eu.xword.nixer.bloom.BloomFilter;

/**
 * Created on 23/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsChecker {

    private final HashFunction hashFunction = Hashing.sha1(); // TODO make this configurable

    private final BloomFilter<byte[]> pwnedFilter;

    public PwnedCredentialsChecker(final BloomFilter<byte[]> pwnedFilter) {
        this.pwnedFilter = pwnedFilter;
    }

    public boolean isPasswordPwned(final String password) {

        final byte[] passwordBytes = password.getBytes(Charsets.UTF_8);

        final byte[] passwordHash = hashFunction.hashBytes(passwordBytes).asBytes();

        return pwnedFilter.mightContain(passwordHash);
    }
}

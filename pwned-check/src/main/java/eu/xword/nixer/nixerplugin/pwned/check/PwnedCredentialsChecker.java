package eu.xword.nixer.nixerplugin.pwned.check;

import eu.xword.nixer.bloom.BloomFilter;
import org.springframework.stereotype.Component;

/**
 * Created on 23/09/2019.
 *
 * @author gcwiak
 */
@Component
public class PwnedCredentialsChecker {

    private final BloomFilter<byte[]> pwnedFilter;

    public PwnedCredentialsChecker(final BloomFilter<byte[]> pwnedFilter) {
        this.pwnedFilter = pwnedFilter;
    }

    public boolean isPwned(final String userName, final String password) {
        // TODO do actual hashing
        return pwnedFilter.mightContain(password.getBytes());
    }
}

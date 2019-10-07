package eu.xword.nixer.nixerplugin.pwned.check;

import eu.xword.nixer.bloom.BloomFilterCheck;
import org.springframework.stereotype.Component;

/**
 * Created on 23/09/2019.
 *
 * @author gcwiak
 */
@Component
public class PwnedCredentialsChecker {

    private final BloomFilterCheck pwnedFilter;

    public PwnedCredentialsChecker(final BloomFilterCheck pwnedFilter) {
        this.pwnedFilter = pwnedFilter;
    }

    public boolean isPasswordPwned(final String password) {
        return pwnedFilter.test(password);
    }
}

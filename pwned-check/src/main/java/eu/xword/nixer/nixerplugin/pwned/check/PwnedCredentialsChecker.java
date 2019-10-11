package eu.xword.nixer.nixerplugin.pwned.check;

import eu.xword.nixer.bloom.check.BloomFilterCheck;

/**
 * Created on 23/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsChecker {

    private final BloomFilterCheck pwnedFilter;
    private final int maxPasswordLength;

    public PwnedCredentialsChecker(final BloomFilterCheck pwnedFilter, final int maxPasswordLength) {
        this.pwnedFilter = pwnedFilter;
        this.maxPasswordLength = maxPasswordLength;
    }

    public boolean isPasswordPwned(final String password) {

        if (password == null || password.length() > maxPasswordLength) {
            return false;
        }

        return pwnedFilter.test(password);
    }
}

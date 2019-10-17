package eu.xword.nixer.nixerplugin.pwned.check;

import eu.xword.nixer.bloom.check.BloomFilterCheck;
import eu.xword.nixer.nixerplugin.metrics.MetricsWriter;

import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.NOT_PWNED_PASSWORD;
import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.PWNED_PASSWORD;

/**
 * Created on 23/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsChecker {

    private final BloomFilterCheck pwnedFilter;
    private final int maxPasswordLength;
    private final MetricsWriter metrics;

    public PwnedCredentialsChecker(final BloomFilterCheck pwnedFilter, final int maxPasswordLength, final MetricsWriter metrics) {
        this.pwnedFilter = pwnedFilter;
        this.maxPasswordLength = maxPasswordLength;
        this.metrics = metrics;
    }

    public boolean isPasswordPwned(final String password) {

        final boolean pwned = isPwned(password);

        if (pwned) {
            metrics.write(PWNED_PASSWORD);
        } else {
            metrics.write(NOT_PWNED_PASSWORD);
        }

        return pwned;
    }

    private boolean isPwned(final String password) {
        if (password == null || password.length() > maxPasswordLength) {
            return false;
        }

        return pwnedFilter.test(password);
    }
}

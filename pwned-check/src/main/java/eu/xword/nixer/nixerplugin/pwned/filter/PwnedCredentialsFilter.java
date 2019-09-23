package eu.xword.nixer.nixerplugin.pwned.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.filter.NixerFilter;
import eu.xword.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;

/**
 * Created on 17/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsFilter extends NixerFilter {

    private final PwnedCredentialsChecker pwnedCredentialsChecker;

    public PwnedCredentialsFilter(final PwnedCredentialsChecker pwnedCredentialsChecker) {
        this.pwnedCredentialsChecker = pwnedCredentialsChecker;
    }

    @Override
    protected boolean applies(final HttpServletRequest request) {
        // TODO retrieve user/password from request
        return pwnedCredentialsChecker.isPwned("usr", "password");
    }
}

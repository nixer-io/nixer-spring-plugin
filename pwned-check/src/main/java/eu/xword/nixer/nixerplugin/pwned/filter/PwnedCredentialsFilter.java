package eu.xword.nixer.nixerplugin.pwned.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.filter.MetadataFilter;
import eu.xword.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;

/**
 * Created on 17/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsFilter extends MetadataFilter {

    private final PwnedCredentialsChecker pwnedCredentialsChecker;

    public PwnedCredentialsFilter(final PwnedCredentialsChecker pwnedCredentialsChecker) {
        this.pwnedCredentialsChecker = pwnedCredentialsChecker;
    }

    @Override
    protected void apply(final HttpServletRequest request) {

        // TODO retrieve password properly
        final String password = request.getParameter("password");

        if (password != null && pwnedCredentialsChecker.isPasswordPwned(password)) {
            request.setAttribute("nixer.pwned.password", true);
        }
    }
}

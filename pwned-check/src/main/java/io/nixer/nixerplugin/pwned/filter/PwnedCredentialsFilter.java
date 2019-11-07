package io.nixer.nixerplugin.pwned.filter;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;
import io.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;

/**
 * Created on 17/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsFilter extends MetadataFilter {

    private final String passwordParameter;

    private final PwnedCredentialsChecker pwnedCredentialsChecker;

    public PwnedCredentialsFilter(final String passwordParameter, final PwnedCredentialsChecker pwnedCredentialsChecker) {
        this.passwordParameter = passwordParameter;
        this.pwnedCredentialsChecker = pwnedCredentialsChecker;
    }

    @Override
    protected void apply(final HttpServletRequest request) {

        final String password = obtainPassword(request);

        if (password != null && pwnedCredentialsChecker.isPasswordPwned(password)) {
            request.setAttribute("nixer.pwned.password", true);
        }
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }
}

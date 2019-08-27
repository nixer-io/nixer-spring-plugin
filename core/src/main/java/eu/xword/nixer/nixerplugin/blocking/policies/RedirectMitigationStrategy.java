package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;

public class RedirectMitigationStrategy implements MitigationStrategy {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String redirectUrl;

    public RedirectMitigationStrategy(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    /**
     * Sets the strategy to be used for redirecting to the required channel URL. A
     * {@code DefaultRedirectStrategy} instance will be used if not set.
     *
     * @param redirectStrategy the strategy instance to which the URL will be passed.
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }
}

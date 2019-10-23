package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;

/**
 * Performs Redirect to given url. This behavior is committing response, meaning it will stop processing of succeeding filter.
 */
public class RedirectBehavior implements Behavior {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String redirectUrl;
    private final String name;

    public RedirectBehavior(final String redirectUrl, final String name) {
        this.redirectUrl = redirectUrl;
        this.name = name;
    }

    public void act(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    @Override
    public boolean isCommitting() {
        return true;
    }

    @Override
    public String name() {
        return name;
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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

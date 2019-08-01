package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;

public class RedirectStrategy implements MitigationStrategy {

    private final org.springframework.security.web.RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String redirectUrl;

    public RedirectStrategy(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}

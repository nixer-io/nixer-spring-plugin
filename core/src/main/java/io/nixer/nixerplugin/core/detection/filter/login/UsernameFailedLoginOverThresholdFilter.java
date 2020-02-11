package io.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.registry.UsernameOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

/**
 * Appends information if presented username is over threshold for failed login.
 */
public class UsernameFailedLoginOverThresholdFilter extends MetadataFilter {

    private final UsernameOverLoginThresholdRegistry usernameOverLoginThresholdRegistry;

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/login", "POST");

    public UsernameFailedLoginOverThresholdFilter(UsernameOverLoginThresholdRegistry usernameOverLoginThresholdRegistry) {
        Assert.notNull(usernameOverLoginThresholdRegistry, "UsernameOverLoginThresholdRegistry must not be null");
        this.usernameOverLoginThresholdRegistry = usernameOverLoginThresholdRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            final String username = request.getParameter(usernameParameter);
            if (username != null && usernameOverLoginThresholdRegistry.contains(username)) {
                request.setAttribute(RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD, true);
            }
        }
    }

    public void setRequestMatcher(final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }
}

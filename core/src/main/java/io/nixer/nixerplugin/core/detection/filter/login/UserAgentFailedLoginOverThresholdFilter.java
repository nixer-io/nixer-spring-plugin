package io.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.UserAgentOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.domain.useragent.UserAgentTokenizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Appends information if presented username is over threshold for failed login.
 */
public class UserAgentFailedLoginOverThresholdFilter extends MetadataFilter {

    private final UserAgentTokenizer userAgentTokenizer;
    private final UserAgentOverLoginThresholdRegistry userAgentOverLoginThresholdRegistry;

    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/login", "POST");

    public UserAgentFailedLoginOverThresholdFilter(final UserAgentTokenizer userAgentTokenizer,
                                                   final UserAgentOverLoginThresholdRegistry userAgentOverLoginThresholdRegistry) {
        this.userAgentTokenizer = userAgentTokenizer;
        this.userAgentOverLoginThresholdRegistry = userAgentOverLoginThresholdRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            final String userAgent = request.getHeader(USER_AGENT);
            final String userAgentToken = userAgentTokenizer.tokenize(userAgent);
            boolean overThreshold = userAgentToken != null && userAgentOverLoginThresholdRegistry.contains(userAgentToken);

            request.setAttribute(RequestMetadata.USER_AGENT_TOKEN, userAgentToken);
            request.setAttribute(RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, overThreshold);
        }
    }
}

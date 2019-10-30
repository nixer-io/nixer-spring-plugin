package eu.xword.nixer.nixerplugin.core.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.core.registry.UserAgentOverLoginThresholdRegistry;
import eu.xword.nixer.nixerplugin.core.useragent.UserAgentTokenizer;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.USER_AGENT_TOKEN;
import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Appends information if presented username is over threshold for failed login.
 */
public class UserAgentFailedLoginOverThresholdFilter extends MetadataFilter {

    private final UserAgentTokenizer userAgentTokenizer = UserAgentTokenizer.sha1Tokenizer();
    private final UserAgentOverLoginThresholdRegistry userAgentOverLoginThresholdRegistry;

    public UserAgentFailedLoginOverThresholdFilter(UserAgentOverLoginThresholdRegistry userAgentOverLoginThresholdRegistry) {
        Assert.notNull(userAgentOverLoginThresholdRegistry, "UsernameOverLoginThresholdRegistry must not be null");
        this.userAgentOverLoginThresholdRegistry = userAgentOverLoginThresholdRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final String userAgent = request.getHeader(USER_AGENT);
        final String userAgentToken = userAgentTokenizer.tokenize(userAgent);
        boolean overThreshold = userAgentToken != null && userAgentOverLoginThresholdRegistry.contains(userAgentToken);

        request.setAttribute(USER_AGENT_TOKEN, userAgentToken);
        request.setAttribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, overThreshold);
    }
}

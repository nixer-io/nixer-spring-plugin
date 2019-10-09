package eu.xword.nixer.nixerplugin.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.registry.BlockedUserRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.USERNAME_BLOCKED;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class UsernameLoginFilter extends MetadataFilter {

    private BlockedUserRegistry blockedUserRegistry;

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

    // TODO externalize
    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/login", "POST");

    public UsernameLoginFilter(BlockedUserRegistry blockedUserRegistry) {
        this.blockedUserRegistry = blockedUserRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            final String username = request.getParameter(usernameParameter);
            if (username != null && blockedUserRegistry.contains(username)) {
                request.setAttribute(USERNAME_BLOCKED, true);
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

package io.nixer.nixerplugin.core.login;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.HttpHeaders;
import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_METADATA;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_TOKEN;

public class LoginContextFactory {

    private final HttpServletRequest request;
    private final LoginFailureTypeRegistry loginFailureTypeRegistry;

    public LoginContextFactory(final HttpServletRequest request,
                               final LoginFailureTypeRegistry loginFailureTypeRegistry) {
        this.request = request;
        this.loginFailureTypeRegistry = loginFailureTypeRegistry;
    }

    LoginContext create(final AbstractAuthenticationEvent event) {
        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        final String ip = request.getRemoteAddr();
        final String username = extractUsername(event);

        final LoginContext context = new LoginContext();
        context.setUsername(username);
        context.setIpAddress(ip);
        context.setUserAgent(userAgent);
        context.setUserAgentToken((String) request.getAttribute(USER_AGENT_TOKEN));
        context.setIpMetadata((IpMetadata) request.getAttribute(IP_METADATA));
        return context;
    }


    private String extractUsername(final AbstractAuthenticationEvent event) {
        // consider other cases
        final Object source = event.getSource();
        if (source instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) source;
            final Object principal = token.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }

        return null;
    }

    LoginResult getLoginResult(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            return LoginResult.success();
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            final AuthenticationException exception = ((AbstractAuthenticationFailureEvent) event).getException();
            final LoginFailureType failureType = loginFailureTypeRegistry.fromException(exception);
            return LoginResult.failure(failureType);
        } else {
            return null;
        }
    }
}

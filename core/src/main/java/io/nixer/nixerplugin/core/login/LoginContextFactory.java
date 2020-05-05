package io.nixer.nixerplugin.core.login;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.HttpHeaders;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
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

    LoginContext create(final AbstractAuthenticationEvent event) throws UnknownAuthenticationEventException {
        final LoginResult loginResult = getLoginResult(event);
        final String username = extractUsername(event);

        if (loginResult == null || username == null) {
            throw new UnknownAuthenticationEventException(String.format("Unable to determine login result [%s] or username [%s] for event %s",
                    loginResult, username, event.getClass()));
        }

        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        final String ip = request.getRemoteAddr();

        final LoginContext context = new LoginContext();
        context.setLoginResult(loginResult);
        context.setUsername(username);
        context.setIpAddress(ip);
        context.setUserAgent(userAgent);
        context.setUserAgentToken((String) request.getAttribute(USER_AGENT_TOKEN));
        context.setIpMetadata((IpMetadata) request.getAttribute(IP_METADATA));
        context.setIpMetadata((IpMetadata) request.getAttribute(IP_METADATA));
        context.setFingerprint((String) request.getAttribute(RequestMetadata.FINGERPRINT_VALUE));
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

    private LoginResult getLoginResult(AbstractAuthenticationEvent event) {
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

package eu.xword.nixer.nixerplugin.core.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;
import eu.xword.nixer.nixerplugin.core.ip.IpMetadata;
import eu.xword.nixer.nixerplugin.core.stigma.StigmaToken;
import eu.xword.nixer.nixerplugin.core.stigma.StigmaUtils;
import eu.xword.nixer.nixerplugin.core.stigma.embed.EmbeddedStigmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.IP_METADATA;
import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.USER_AGENT_TOKEN;

/**
 * Listens for Spring {@link AbstractAuthenticationEvent} and processed them.
 */
@Component
public class LoginActivityListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private final StigmaUtils stigmaUtils;

    private final EmbeddedStigmaService stigmaService;

    private final LoginActivityService loginActivityService;

    private final LoginFailureTypeRegistry loginFailureTypeRegistry;

    public LoginActivityListener(final EmbeddedStigmaService stigmaService,
                                 final StigmaUtils stigmaUtils,
                                 final LoginActivityService loginActivityService,
                                 final LoginFailureTypeRegistry loginFailureTypeRegistry) {
        this.stigmaUtils = stigmaUtils;
        this.stigmaService = stigmaService;
        this.loginActivityService = loginActivityService;
        this.loginFailureTypeRegistry = loginFailureTypeRegistry;
    }

    private void handleStigma(LoginResult loginResult, LoginContext context) {
        final StigmaToken receivedStigma = stigmaUtils.findStigma(request);
        final StigmaToken newStigma = stigmaService.refreshStigma(receivedStigma, loginResult, context);

        if (!newStigma.equals(receivedStigma)) {
            stigmaUtils.setStigmaCookie(response, newStigma);
        }
    }

    private void reportLogin(LoginResult loginResult, final LoginContext context) {
        loginActivityService.save(loginResult, context);
    }

    private LoginContext buildContext(final AbstractAuthenticationEvent event) {
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


    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        final LoginResult loginResult = getLoginResult(event);
        if (loginResult != null) {
            final LoginContext context = buildContext(event);
            reportLogin(loginResult, context);
            handleStigma(loginResult, context);
        }
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

    @Autowired
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setResponse(final HttpServletResponse response) {
        this.response = response;
    }
}

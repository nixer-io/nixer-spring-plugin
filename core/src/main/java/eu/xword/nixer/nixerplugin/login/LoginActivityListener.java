package eu.xword.nixer.nixerplugin.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;
import eu.xword.nixer.nixerplugin.UserUtils;
import eu.xword.nixer.nixerplugin.filter.RequestAugmentation;
import eu.xword.nixer.nixerplugin.ip.IpMetadata;
import eu.xword.nixer.nixerplugin.stigma.StigmaToken;
import eu.xword.nixer.nixerplugin.stigma.StigmaUtils;
import eu.xword.nixer.nixerplugin.stigma.embed.EmbeddedStigmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Listens for Spring {@link AbstractAuthenticationEvent} and stores it in configured repositories
 */
@Component
public class LoginActivityListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private StigmaUtils stigmaUtils;
    private EmbeddedStigmaService stigmaService;

    private LoginActivityService loginActivityService;

    private LoginFailureTypeRegistry loginFailureTypeRegistry;

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
        loginActivityService.handle(loginResult, context);
    }

    private LoginContext buildContext() {
        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        final String ip = request.getRemoteAddr();

        final String username = UserUtils.extractUsername(request);

        final LoginContext context = new LoginContext(username, ip, userAgent);
        final IpMetadata ipMetadata = (IpMetadata) request.getAttribute(RequestAugmentation.IP_METADATA);
        context.setIpMetadata(ipMetadata);
        return context;
    }


    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        final LoginResult loginResult = getLoginResult(event);

        if (loginResult != null) {
            final LoginContext context = buildContext();
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

}

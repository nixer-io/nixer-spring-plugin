package eu.xword.nixer.nixerplugin.stigma;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;
import eu.xword.nixer.nixerplugin.LoginContext;
import eu.xword.nixer.nixerplugin.LoginResult;
import eu.xword.nixer.nixerplugin.UserUtils;
import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;
import eu.xword.nixer.nixerplugin.stigma.embedd.EmbeddedStigmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginStigmaListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private StigmaSource stigmaSource;
    private EmbeddedStigmaService stigmaService;


    private ConcurrentHashMap<String, Integer> loginAttemptsByIp = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> loginAttemptsByUsername = new ConcurrentHashMap<>();

    //TODO make thresholds configurable
    private static final int LOGIN_FAILED_BY_IP_THRESHOLD = 50;
    private static final int LOGIN_FAILED_BY_USER_THRESHOLD = 5;


    public LoginStigmaListener(final EmbeddedStigmaService stigmaService,
                               final StigmaSource stigmaSource) {
        this.stigmaSource = stigmaSource;
        this.stigmaService = stigmaService;
    }

    private void handleStigma(LoginResult loginResult) {
        final StigmaToken receivedStigma = stigmaSource.findStigma(request);
        final LoginContext context = buildContext();

        //TODO extract keeping track of stats to dedicated place
        if (loginResult == LoginResult.SUCCESS) {
            loginAttemptsByIp.remove(context.getIpAddress());
            //TODO check username for null
            if (context.getUsername() != null) {
                loginAttemptsByUsername.remove(context.getUsername());
            }
        } else {
            final Integer failedByIp = loginAttemptsByIp.compute(context.getIpAddress(), ZERO_OR_INCRY);
            // TODO make sure we don't trigger block event twice
            if (failedByIp > LOGIN_FAILED_BY_IP_THRESHOLD) {
                eventPublisher.publishEvent(new BlockSourceIPEvent(context.getIpAddress()));
            }

            if (context.getUsername() != null) {
                final Integer failedByUser = loginAttemptsByUsername.compute(context.getUsername(), ZERO_OR_INCRY);
                if (failedByUser > LOGIN_FAILED_BY_USER_THRESHOLD) {
                    eventPublisher.publishEvent(new LockUserEvent(context.getUsername()));
                }
            }
        }

        final StigmaToken newStigma = stigmaService.refreshStigma(receivedStigma, loginResult, context);

        if (!newStigma.equals(receivedStigma)) {
            stigmaSource.setStigmaCookie(response, newStigma);
        }
    }

    private static final BiFunction<String, Integer, Integer> ZERO_OR_INCRY = (key, count) -> count == null ? 0 : count + 1;

    private LoginContext buildContext() {
        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        final String remoteAddr = request.getRemoteAddr();
//        request.getHeader(HttpHeaders.X_FORWARDED_FOR);
        //TODO handle x-forwarded-for and x-forwarded

        final String username = UserUtils.extractUsername(request);

        return new LoginContext(username, remoteAddr, userAgent);
    }


    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        final LoginResult authenticationResult = event instanceof AuthenticationSuccessEvent
                ? LoginResult.SUCCESS
                : (
                event instanceof AbstractAuthenticationFailureEvent
                        ? LoginResult.FAILURE
                        : null
        );

        if (authenticationResult != null) {
            handleStigma(authenticationResult);
        }
    }


}

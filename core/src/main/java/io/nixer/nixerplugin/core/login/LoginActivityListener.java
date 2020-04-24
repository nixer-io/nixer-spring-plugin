package io.nixer.nixerplugin.core.login;

import java.util.Collections;
import java.util.List;

import io.nixer.nixerplugin.core.detection.rules.RulesRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;

/**
 * Listens for Spring {@link AbstractAuthenticationEvent} and delegates processing to, possibly multiple, {@link LoginActivityHandler}s.
 */
public class LoginActivityListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Log logger = LogFactory.getLog(LoginActivityListener.class);

    private final LoginContextFactory loginContextFactory;

    private final List<LoginActivityHandler> loginActivityHandlers;

    private final RulesRunner rulesRunner;

    public LoginActivityListener(final LoginContextFactory loginContextFactory,
                                 final List<LoginActivityHandler> loginActivityHandlers,
                                 final RulesRunner rulesRunner) {
        this.loginContextFactory = loginContextFactory;
        this.loginActivityHandlers = Collections.unmodifiableList(loginActivityHandlers);
        this.rulesRunner = rulesRunner;
    }

    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {

        try {
            final LoginContext context = loginContextFactory.create(event);

            loginActivityHandlers.forEach(it -> it.handle(context));

            rulesRunner.onLogin(context);

        } catch (UnknownAuthenticationEventException exception) {
            logger.trace("Ignored, unknown authentication event", exception);
        }


    }
}

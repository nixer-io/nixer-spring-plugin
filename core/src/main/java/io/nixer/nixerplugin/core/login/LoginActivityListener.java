package io.nixer.nixerplugin.core.login;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.util.Assert;

/**
 * Listens for Spring {@link AbstractAuthenticationEvent} and delegates processing to, possibly multiple, {@link LoginActivityHandler}s.
 */
public class LoginActivityListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Log logger = LogFactory.getLog(LoginActivityListener.class);

    private final LoginContextFactory loginContextFactory;

    private final List<LoginActivityHandler> loginActivityHandlers;

    public LoginActivityListener(final LoginContextFactory loginContextFactory,
                                 final List<LoginActivityHandler> loginActivityHandlers) {
        Assert.notNull(loginContextFactory, "LoginContextFactory must not be null");
        this.loginContextFactory = loginContextFactory;

        Assert.notNull(loginActivityHandlers, "loginActivityHandlers must not be null");
        this.loginActivityHandlers = Collections.unmodifiableList(loginActivityHandlers);
    }

    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {

        try {
            final LoginContext context = loginContextFactory.create(event);
            loginActivityHandlers.forEach(it -> it.handle(context));
        } catch (UnknownAuthenticationEventException exception) {
            logger.warn("Failed to handle authentication event", exception);
        }


    }
}

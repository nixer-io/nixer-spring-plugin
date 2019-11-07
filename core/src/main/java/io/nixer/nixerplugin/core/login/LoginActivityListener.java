package io.nixer.nixerplugin.core.login;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.util.Assert;

/**
 * Listens for Spring {@link AbstractAuthenticationEvent} and processed them.
 */
public class LoginActivityListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final LoginActivityService loginActivityService;
    private final LoginContextFactory loginContextFactory;

    public LoginActivityListener(final LoginActivityService loginActivityService,
                                 final LoginContextFactory loginContextFactory) {
        Assert.notNull(loginActivityService, "LoginActivityService must not be null");
        this.loginActivityService = loginActivityService;

        Assert.notNull(loginContextFactory, "LoginContextFactory must not be null");
        this.loginContextFactory = loginContextFactory;
    }

    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        final LoginResult loginResult = loginContextFactory.getLoginResult(event);
        if (loginResult != null) {
            final LoginContext context = loginContextFactory.create(event);
            loginActivityService.save(loginResult, context);
        }
    }
}

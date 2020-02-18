package io.nixer.nixerplugin.captcha.events;

import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class FailedCaptchaAuthenticationEvent extends AbstractAuthenticationFailureEvent {

    public FailedCaptchaAuthenticationEvent(final Authentication authentication, final AuthenticationException exception) {
        super(authentication, exception);
    }
}

package io.nixer.nixerplugin.captcha.security;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.ProviderManager;

/**
 * Used to break authentication loop in Spring Security {@link ProviderManager} mechanism when captcha is invalid.
 */
public class CaptchaAuthenticationStatusException extends AccountStatusException {

    public CaptchaAuthenticationStatusException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public CaptchaAuthenticationStatusException(final String msg) {
        super(msg);
    }
}

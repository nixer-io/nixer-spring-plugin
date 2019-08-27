package eu.xword.nixer.nixerplugin.captcha.security;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Thrown if an authentication request is rejected because the captcha is invalid.
 * For this exception to be thrown, it means that either captcha response was missing, incorrect or verification failed.
 */
public class BadCaptchaException extends BadCredentialsException {
    public BadCaptchaException(final String msg) {
        super(msg);
    }

    public BadCaptchaException(final String msg, final Throwable t) {
        super(msg, t);
    }
}

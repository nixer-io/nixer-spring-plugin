package io.nixer.nixerplugin.captcha.error;

/**
 * Thrown if an captcha verification failed
 */
public abstract class CaptchaException extends RuntimeException {

    public CaptchaException(final String message) {
        super(message);
    }

    public CaptchaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package eu.xword.nixer.nixerplugin.captcha.error;

/**
 * Thrown if an captcha verification failed
 */
public abstract class RecaptchaException extends RuntimeException {

    public RecaptchaException(final String message) {
        super(message);
    }

    public RecaptchaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

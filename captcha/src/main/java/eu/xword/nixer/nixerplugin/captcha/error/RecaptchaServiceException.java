package eu.xword.nixer.nixerplugin.captcha.error;

/**
 * Thrown if an eu.xword.nixer.nixerplugin.captcha verification failed due to communication error.
 * For this exception to be thrown, it means that request failed as result of timeout, connectivity etc.
 */
public class RecaptchaServiceException extends RecaptchaException {
    public RecaptchaServiceException(final String message) {
        super(message);
    }

    public RecaptchaServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

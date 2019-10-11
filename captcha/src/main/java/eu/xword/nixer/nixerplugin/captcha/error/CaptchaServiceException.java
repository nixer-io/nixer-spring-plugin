package eu.xword.nixer.nixerplugin.captcha.error;

/**
 * Thrown if an captcha verification failed due to communication error.
 * For this exception to be thrown, it means that request failed as result of timeout, connectivity etc.
 */
public class CaptchaServiceException extends CaptchaException {
    public CaptchaServiceException(final String message) {
        super(message);
    }

    public CaptchaServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

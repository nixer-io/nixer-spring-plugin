package eu.xword.nixer.nixerplugin.captcha.error;

/**
 * Thrown if an captcha verification failed due to client error.
 * For this exception to be thrown, it means that verification request was missing mandatory parameters.
 */
public class CaptchaClientException extends CaptchaException {
    public CaptchaClientException(final String message) {
        super(message);
    }

}

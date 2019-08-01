package eu.xword.nixer.nixerplugin.captcha.error;

public class RecaptchaServiceException extends RuntimeException {
    public RecaptchaServiceException(final String message) {
        super(message);
    }

    public RecaptchaServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RecaptchaServiceException(final Throwable cause) {
        super(cause);
    }
}

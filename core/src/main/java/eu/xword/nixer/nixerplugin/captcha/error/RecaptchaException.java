package eu.xword.nixer.nixerplugin.captcha.error;

public abstract class RecaptchaException extends RuntimeException {

    public RecaptchaException(final String message) {
        super(message);
    }

    public RecaptchaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package eu.xword.nixer.nixerplugin.captcha.error;

public class RecaptchaServiceException extends RecaptchaException {
    public RecaptchaServiceException(final String message) {
        super(message);
    }

    public RecaptchaServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

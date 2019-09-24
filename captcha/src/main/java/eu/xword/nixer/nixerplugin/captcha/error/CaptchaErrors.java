package eu.xword.nixer.nixerplugin.captcha.error;

/**
 * Helper factory methods for creating variants of {@link RecaptchaException}.
 */
public class CaptchaErrors {

    public static RecaptchaClientException invalidCaptchaFormat(String message) {
        return new RecaptchaClientException(message);
    }

    public static RecaptchaClientException invalidRecaptcha(String message) {
        return new RecaptchaClientException(message);
    }

    public static RecaptchaServiceException serviceFailure(String message, Exception e) {
        return new RecaptchaServiceException(message, e);
    }

}

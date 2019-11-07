package io.nixer.nixerplugin.captcha.error;

/**
 * Helper factory methods for creating variants of {@link CaptchaException}.
 */
public class CaptchaErrors {

    public static CaptchaClientException invalidCaptchaFormat(String message) {
        return new CaptchaClientException(message);
    }

    public static CaptchaClientException invalidCaptcha(String message) {
        return new CaptchaClientException(message);
    }

    public static CaptchaServiceException serviceFailure(String message, Exception e) {
        return new CaptchaServiceException(message, e);
    }

}

package eu.xword.nixer.nixerplugin.captcha.metrics;

/**
 * Place for constants used to report metrics
 */
public final class CaptchaMetrics {

    private CaptchaMetrics() {
    }

    public static final String LOGIN_ACTION = "login";

    static final String CAPTCHA_METRIC = "captcha";
    static final String RESULT_TAG = "result";
    static final String ACTION_TAG = "action";
    static final String CAPTCHA_FAILED = "failed";
    static final String CAPTCHA_PASSED = "passed";
    static final String CAPTCHA_PASSED_DESC = "Captcha passes";
    static final String CAPTCHA_FAILED_DESC = "Captcha failed";

}

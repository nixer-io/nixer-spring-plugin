package eu.xword.nixer.nixerplugin.captcha.metrics;

public final class CaptchaMetrics {

    private CaptchaMetrics() {
    }
    public static final String LOGIN_ACTION = "login";

    static final String CAPTCHA_COUNTER = "captcha";
    static final String RESULT_TAG = "result";
    static final String ACTION_TAG = "action";
    static final String RESULT_FAILED = "failed";
    static final String RESULT_PASSED = "passed";
    static final String CAPTCHA_PASS_DESC = "Captcha passes";
    static final String CAPTCHA_FAILED_DESC = "Captcha failed";

}

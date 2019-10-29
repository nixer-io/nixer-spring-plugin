package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.core.metrics.CounterDefinition;
import io.micrometer.core.instrument.Counter;

/**
 * Defines counters reported for captcha
 */
public enum CaptchaCounters {

    CAPTCHA_PASSED {
        public CounterDefinition counter(final String action) {
            return meterRegistry -> Counter.builder(CAPTCHA_METRIC)
                    .description(CAPTCHA_PASSED_DESC)
                    .tag(RESULT_TAG, CAPTCHA_RESULT_PASSED)
                    .tag(ACTION_TAG, action)
                    .register(meterRegistry);
        }
    },
    CAPTCHA_FAILED {
        public CounterDefinition counter(final String action) {
            return meterRegistry -> Counter.builder(CAPTCHA_METRIC)
                    .description(CAPTCHA_FAILED_DESC)
                    .tag(RESULT_TAG, CAPTCHA_RESULT_FAILED)
                    .tag(ACTION_TAG, action)
                    .register(meterRegistry);
        }
    };

    public static final String LOGIN_ACTION = "login";

    static final String CAPTCHA_METRIC = "captcha";
    static final String RESULT_TAG = "result";
    static final String ACTION_TAG = "action";
    static final String CAPTCHA_RESULT_FAILED = "failed";
    static final String CAPTCHA_RESULT_PASSED = "passed";
    static final String CAPTCHA_PASSED_DESC = "Captcha passes";
    static final String CAPTCHA_FAILED_DESC = "Captcha failed";


    public abstract CounterDefinition counter(final String action);
}

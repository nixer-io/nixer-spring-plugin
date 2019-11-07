package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.core.metrics.CounterDefinition;
import io.micrometer.core.instrument.Counter;
import org.springframework.util.Assert;

/**
 * Defines counters reported for captcha
 */
enum CaptchaCounters {

    CAPTCHA_PASSED {
        public CounterDefinition counter(final String action) {
            Assert.notNull(action, "Action must not be null");

            return meterRegistry -> Counter.builder(CAPTCHA_METRIC)
                    .description(CAPTCHA_PASSED_DESC)
                    .tag(RESULT_TAG, CAPTCHA_RESULT_PASSED)
                    .tag(ACTION_TAG, action)
                    .register(meterRegistry);
        }
    },
    CAPTCHA_FAILED {
        public CounterDefinition counter(final String action) {
            Assert.notNull(action, "Action must not be null");

            return meterRegistry -> Counter.builder(CAPTCHA_METRIC)
                    .description(CAPTCHA_FAILED_DESC)
                    .tag(RESULT_TAG, CAPTCHA_RESULT_FAILED)
                    .tag(ACTION_TAG, action)
                    .register(meterRegistry);
        }
    };

    private static final String CAPTCHA_METRIC = "captcha";
    private static final String RESULT_TAG = "result";
    private static final String ACTION_TAG = "action";
    private static final String CAPTCHA_RESULT_FAILED = "failed";
    private static final String CAPTCHA_RESULT_PASSED = "passed";
    private static final String CAPTCHA_PASSED_DESC = "Captcha passes";
    private static final String CAPTCHA_FAILED_DESC = "Captcha failed";


    public abstract CounterDefinition counter(final String action);
}

package eu.xword.nixer.nixerplugin.core.login.metrics;

import java.util.stream.Stream;

import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.metrics.CounterDefinition;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsLookupId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.BAD_PASSWORD;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.DISABLED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.EXPIRED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.INVALID_CAPTCHA;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.LOCKED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.OTHER;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.UNKNOWN_USER;

/**
 * Defines metrics reported for login
 */
public enum LoginMetrics implements CounterDefinition, MetricsLookupId {

    LOGIN_SUCCESS(),
    LOGIN_FAILED_UNKNOWN_USER(UNKNOWN_USER),
    LOGIN_FAILED_BAD_PASSWORD(BAD_PASSWORD),
    LOGIN_FAILED_INVALID_CAPTCHA(INVALID_CAPTCHA),
    LOGIN_FAILED_LOCKED(LOCKED),
    LOGIN_FAILED_EXPIRED(EXPIRED),
    LOGIN_FAILED_DISABLED(DISABLED),
    LOGIN_FAILED_OTHER(OTHER);

    private static final String LOGIN_METRIC = "login";
    private static final String RESULT_TAG = "result";
    private static final String FAILED_LOGIN_DESC = "User login failed";
    private static final String LOGIN_SUCCESS_DESC = "User login succeeded";
    private static final String SUCCESS_TAG = "success";
    private static final String FAILED_TAG = "failed";
    private static final String REASON_TAG = "reason";

    private final CounterDefinition counterDefinition;
    private final LoginFailureType loginFailureType;

    LoginMetrics() {
        this.counterDefinition = successCounter();
        this.loginFailureType = null;
    }

    LoginMetrics(LoginFailureType loginFailureType) {
        this.counterDefinition = failureCounter(loginFailureType);
        this.loginFailureType = loginFailureType;
    }

    private static CounterDefinition successCounter() {
        return meterRegistry -> Counter.builder(LOGIN_METRIC)
                .description(LOGIN_SUCCESS_DESC)
                .tags(RESULT_TAG, SUCCESS_TAG)
                .register(meterRegistry);
    }

    private static CounterDefinition failureCounter(final LoginFailureType reason) {
        return meterRegistry -> Counter.builder(LOGIN_METRIC)
                .description(FAILED_LOGIN_DESC)
                .tags(RESULT_TAG, FAILED_TAG)
                .tag(REASON_TAG, reason.name())
                .register(meterRegistry);
    }

    @Override
    public Counter register(final MeterRegistry meterRegistry) {
        return counterDefinition.register(meterRegistry);
    }

    @Override
    public String lookupId() {
        return name();
    }

    public static LoginMetrics metricFromLoginFailure(final LoginFailureType loginFailureType) {
        return Stream.of(values())
                .filter(loginMetrics -> loginMetrics.loginFailureType == loginFailureType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown loginFailure " + loginFailureType));
    }
}

package io.nixer.nixerplugin.core.login.metrics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.nixer.nixerplugin.core.login.LoginFailureType;
import io.nixer.nixerplugin.core.metrics.CounterDefinition;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.nixer.nixerplugin.core.login.LoginFailureType;

import static io.nixer.nixerplugin.core.login.LoginFailureType.BAD_PASSWORD;
import static io.nixer.nixerplugin.core.login.LoginFailureType.DISABLED;
import static io.nixer.nixerplugin.core.login.LoginFailureType.EXPIRED;
import static io.nixer.nixerplugin.core.login.LoginFailureType.INVALID_CAPTCHA;
import static io.nixer.nixerplugin.core.login.LoginFailureType.LOCKED;
import static io.nixer.nixerplugin.core.login.LoginFailureType.OTHER;
import static io.nixer.nixerplugin.core.login.LoginFailureType.UNKNOWN_USER;

/**
 * Defines counters reported for login
 */
public enum LoginCounters implements CounterDefinition {

    LOGIN_SUCCESS(),
    LOGIN_FAILED_UNKNOWN_USER(LoginFailureType.UNKNOWN_USER),
    LOGIN_FAILED_BAD_PASSWORD(LoginFailureType.BAD_PASSWORD),
    LOGIN_FAILED_INVALID_CAPTCHA(LoginFailureType.INVALID_CAPTCHA),
    LOGIN_FAILED_LOCKED(LoginFailureType.LOCKED),
    LOGIN_FAILED_EXPIRED(LoginFailureType.EXPIRED),
    LOGIN_FAILED_DISABLED(LoginFailureType.DISABLED),
    LOGIN_FAILED_OTHER(LoginFailureType.OTHER);

    private static final String LOGIN_METRIC = "login";
    private static final String RESULT_TAG = "result";
    private static final String FAILED_LOGIN_DESC = "User login failed";
    private static final String LOGIN_SUCCESS_DESC = "User login succeeded";
    private static final String SUCCESS_TAG_VALUE = "success";
    private static final String FAILED_TAG_VALUE = "failed";
    private static final String REASON_TAG = "reason";

    private final CounterDefinition counterDefinition;
    private final LoginFailureType loginFailureType;

    LoginCounters() {
        this.counterDefinition = successCounter();
        this.loginFailureType = null;
    }

    LoginCounters(LoginFailureType loginFailureType) {
        this.counterDefinition = failureCounter(loginFailureType);
        this.loginFailureType = loginFailureType;
    }

    public LoginFailureType loginFailureType() {
        return loginFailureType;
    }

    private static CounterDefinition successCounter() {
        return meterRegistry -> Counter.builder(LOGIN_METRIC)
                .description(LOGIN_SUCCESS_DESC)
                .tags(RESULT_TAG, SUCCESS_TAG_VALUE)
                .register(meterRegistry);
    }

    private static CounterDefinition failureCounter(final LoginFailureType reason) {
        return meterRegistry -> Counter.builder(LOGIN_METRIC)
                .description(FAILED_LOGIN_DESC)
                .tag(RESULT_TAG, FAILED_TAG_VALUE)
                .tag(REASON_TAG, reason.name())
                .register(meterRegistry);
    }

    public static List<LoginCounters> failureCounters() {
        return Stream.of(values())
                .filter(loginCounters -> loginCounters.loginFailureType != null)
                .collect(Collectors.toList());
    }

    @Override
    public Counter register(final MeterRegistry meterRegistry) {
        return counterDefinition.register(meterRegistry);
    }
}

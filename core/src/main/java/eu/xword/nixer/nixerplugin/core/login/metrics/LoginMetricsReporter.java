package eu.xword.nixer.nixerplugin.core.login.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import eu.xword.nixer.nixerplugin.core.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

/**
 * Records metrics about user login in micrometer.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final Counter loginSuccessCounter;

    private final Map<LoginFailureType, Counter> failureCounters;

    private final MeterRegistry meterRegistry;

    public LoginMetricsReporter(final MeterRegistry meterRegistry, final LoginFailureTypeRegistry loginFailureTypeRegistry) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");
        this.meterRegistry = meterRegistry;

        Assert.notNull(loginFailureTypeRegistry, "LoginFailureTypeRegistry must not be null");

        final Map<LoginFailureType, Counter> failureCounters = loginFailureTypeRegistry.getReasons().stream()
                .collect(Collectors.toMap(it -> it, this::failureCounter));
        this.failureCounters = Collections.unmodifiableMap(failureCounters);

        this.loginSuccessCounter = Counter.builder("login")
                .description("User login succeeded")
                .tags("result", "success")
                .register(meterRegistry);

    }

    private Counter failureCounter(final LoginFailureType reason) {
        return Counter.builder("login")
                .description("User login failed")
                .tags("result", "failed")
                .tag("reason", reason.name())
                .register(meterRegistry);
    }

    private void reportLoginFail(final LoginFailureType loginFailureType) {
        final Counter failureCounter = failureCounters.get(loginFailureType);

        failureCounter.increment();
    }

    private void reportLoginSuccess() {
        loginSuccessCounter.increment();
    }

    @Override
    public void save(final LoginResult loginResult, final LoginContext loginContext) {
        loginResult
                .onSuccess(it -> reportLoginSuccess())
                .onFailure(result -> reportLoginFail(result.getFailureType()));
    }
}

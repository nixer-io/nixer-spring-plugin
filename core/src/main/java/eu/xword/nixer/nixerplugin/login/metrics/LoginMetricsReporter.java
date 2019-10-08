package eu.xword.nixer.nixerplugin.login.metrics;

import java.util.HashMap;

import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginFailureTypeRegistry;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

/**
 * Records metrics about user login in micrometer.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final Counter loginSuccessCounter;

    private final HashMap<LoginFailureType, Counter> failureCounters = new HashMap<>();

    private final MeterRegistry meterRegistry;

    public LoginMetricsReporter(final MeterRegistry meterRegistry, final LoginFailureTypeRegistry loginFailureTypeRegistry) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");
        this.meterRegistry = meterRegistry;

        Assert.notNull(loginFailureTypeRegistry, "LoginFailureTypeRegistry must not be null");

        loginFailureTypeRegistry.getReasons()
                .forEach(reason -> failureCounters.put(reason, this.failureCounter(reason)));

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
    public void reportLoginActivity(final LoginResult loginResult, final LoginContext loginContext) {
        loginResult
                .onSuccess(it -> reportLoginSuccess())
                .onFailure(result -> reportLoginFail(result.getFailureType()));
    }
}

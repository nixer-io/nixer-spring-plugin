package eu.xword.nixer.nixerplugin.login.metrics;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.Maps;
import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Records metrics about user login in micrometer.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final Counter loginSuccessCounter;

    private final Map<LoginFailureType, Counter> failureCounters;

    public LoginMetricsReporter(final MeterRegistry meterRegistry) {
        final EnumMap<LoginFailureType, Counter> result = Maps.newEnumMap(LoginFailureType.class);

        for (LoginFailureType it : LoginFailureType.values()) {
            final Counter counter = Counter.builder("login")
                    .description("User login failed")
                    .tags("result", "failed")
                    .tag("reason", it.name())
                    .register(meterRegistry);
            result.put(it, counter);
        }
        this.failureCounters = result;

        this.loginSuccessCounter = Counter.builder("login")
                .description("User login succeeded")
                .tags("result", "success")
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

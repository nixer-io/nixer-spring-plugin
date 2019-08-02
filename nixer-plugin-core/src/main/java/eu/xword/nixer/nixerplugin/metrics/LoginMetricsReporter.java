package eu.xword.nixer.nixerplugin.metrics;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.Maps;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class LoginMetricsReporter {

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

    public void reportLoginResult(final LoginResult loginResult) {
        loginResult
                .onSuccess(it -> reportLoginSuccess())
                .onFailure(result -> reportLoginFail(result.getFailureType()));
    }

    private void reportLoginFail(final LoginFailureType loginFailureType) {
        final Counter failureCounter = failureCounters.get(loginFailureType);

        failureCounter.increment();
    }

    private void reportLoginSuccess() {
        loginSuccessCounter.increment();
    }

}

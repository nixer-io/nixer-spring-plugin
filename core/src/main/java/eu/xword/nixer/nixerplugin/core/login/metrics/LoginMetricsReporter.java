package eu.xword.nixer.nixerplugin.core.login.metrics;

import eu.xword.nixer.nixerplugin.core.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsCounter;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.login.metrics.LoginCounters.LOGIN_SUCCESS;
import static eu.xword.nixer.nixerplugin.core.login.metrics.LoginCounters.metricFromLoginFailure;

/**
 * Reports metrics about user login.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final MetricsCounter loginSuccessCounter;
    private final MetricsFactory metricsFactory;

    public LoginMetricsReporter(final MetricsFactory metricsFactory) {
        Assert.notNull(metricsFactory, "MetricsFactory must not be null");

        this.loginSuccessCounter = metricsFactory.counter(LOGIN_SUCCESS.counterDefinition());
        this.metricsFactory = metricsFactory;
    }

    private void reportLoginFail(final LoginFailureType loginFailureType) {
        final MetricsCounter failureCounter = metricsFactory.counter(metricFromLoginFailure(loginFailureType).counterDefinition());
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

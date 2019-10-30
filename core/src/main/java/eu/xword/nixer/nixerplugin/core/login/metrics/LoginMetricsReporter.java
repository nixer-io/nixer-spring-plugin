package eu.xword.nixer.nixerplugin.core.login.metrics;

import java.util.Map;
import java.util.stream.Collectors;

import eu.xword.nixer.nixerplugin.core.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsCounter;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.login.metrics.LoginCounters.LOGIN_SUCCESS;

/**
 * Reports metrics about user login.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final MetricsCounter loginSuccessCounter;
    private final Map<LoginFailureType, MetricsCounter> counterByFailureType;

    public LoginMetricsReporter(final MetricsFactory metricsFactory) {
        Assert.notNull(metricsFactory, "MetricsFactory must not be null");
        this.loginSuccessCounter = metricsFactory.counter(LOGIN_SUCCESS);

        this.counterByFailureType = LoginCounters.failureCounters()
                .stream()
                .collect(Collectors.toMap(
                        LoginCounters::loginFailureType,
                        metricsFactory::counter)
                );
    }

    private void reportLoginFail(final LoginFailureType loginFailureType) {
        final MetricsCounter failureCounter = counterByFailureType.get(loginFailureType);
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

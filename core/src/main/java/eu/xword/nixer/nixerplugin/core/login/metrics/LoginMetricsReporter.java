package eu.xword.nixer.nixerplugin.core.login.metrics;

import eu.xword.nixer.nixerplugin.core.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsWriter;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.login.metrics.LoginMetrics.LOGIN_SUCCESS;
import static eu.xword.nixer.nixerplugin.core.login.metrics.LoginMetrics.metricFromLoginFailure;

/**
 * Records metrics about user login in micrometer.
 */
public class LoginMetricsReporter implements LoginActivityRepository {

    private final MetricsWriter metricsWriter;

    public LoginMetricsReporter(final MetricsWriter metricsWriter) {
        Assert.notNull(metricsWriter, "MetricsWriter must not be null");
        this.metricsWriter = metricsWriter;
    }


    private void reportLoginFail(final LoginFailureType loginFailureType) {
        metricsWriter.write(metricFromLoginFailure(loginFailureType));
    }

    private void reportLoginSuccess() {
        metricsWriter.write(LOGIN_SUCCESS);
    }

    @Override
    public void save(final LoginResult loginResult, final LoginContext loginContext) {
        loginResult
                .onSuccess(it -> reportLoginSuccess())
                .onFailure(result -> reportLoginFail(result.getFailureType()));
    }
}

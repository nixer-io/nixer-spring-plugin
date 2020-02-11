package io.nixer.nixerplugin.core.login;

import java.util.List;

import io.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import org.springframework.util.Assert;

public class LoginActivityService implements LoginActivityHandler {

    private final List<LoginActivityRepository> repositories;

    private final AnomalyRulesRunner anomalyRulesRunner;

    public LoginActivityService(final List<LoginActivityRepository> repositories, final AnomalyRulesRunner anomalyRulesRunner) {
        Assert.notNull(repositories, "Repositories must not be null");
        this.repositories = repositories;

        Assert.notNull(anomalyRulesRunner, "RulesRunner must not be null");
        this.anomalyRulesRunner = anomalyRulesRunner;
    }

    @Override
    public void handle(final LoginResult loginResult, final LoginContext context) {
        for (LoginActivityRepository repository : repositories) {
            repository.save(loginResult, context);
        }

        anomalyRulesRunner.onLogin(context);
    }
}

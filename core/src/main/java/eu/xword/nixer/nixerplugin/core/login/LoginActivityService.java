package eu.xword.nixer.nixerplugin.core.login;

import java.util.List;

import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LoginActivityService {

    private final List<LoginActivityRepository> repositories;

    private final AnomalyRulesRunner anomalyRulesRunner;


    public LoginActivityService(final List<LoginActivityRepository> repositories, final AnomalyRulesRunner anomalyRulesRunner) {
        Assert.notNull(repositories, "Repositories must not be null");
        this.repositories = repositories;

        Assert.notNull(anomalyRulesRunner, "RulesRunner must not be null");
        this.anomalyRulesRunner = anomalyRulesRunner;
    }

    public void save(final LoginResult loginResult, final LoginContext context) {

        for (LoginActivityRepository repository : repositories) {
            repository.save(loginResult, context);
        }

        anomalyRulesRunner.onLogin(context);
    }
}

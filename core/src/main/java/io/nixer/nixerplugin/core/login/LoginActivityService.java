package io.nixer.nixerplugin.core.login;

import java.util.List;

import io.nixer.nixerplugin.core.detection.rules.RulesRunner;
import org.springframework.util.Assert;

public class LoginActivityService implements LoginActivityHandler {

    private final List<LoginActivityRepository> repositories;

    private final RulesRunner rulesRunner;

    public LoginActivityService(final List<LoginActivityRepository> repositories, final RulesRunner rulesRunner) {
        Assert.notNull(repositories, "Repositories must not be null");
        this.repositories = repositories;

        Assert.notNull(rulesRunner, "RulesRunner must not be null");
        this.rulesRunner = rulesRunner;
    }

    @Override
    public void handle(final LoginContext context) {
        for (LoginActivityRepository repository : repositories) {
            repository.save(context);
        }

        rulesRunner.onLogin(context);
    }
}

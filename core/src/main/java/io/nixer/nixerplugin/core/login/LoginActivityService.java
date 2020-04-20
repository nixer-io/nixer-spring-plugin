package io.nixer.nixerplugin.core.login;

import java.util.List;

import org.springframework.util.Assert;

public class LoginActivityService implements LoginActivityHandler {

    private final List<LoginActivityRepository> repositories;

    public LoginActivityService(final List<LoginActivityRepository> repositories) {
        Assert.notNull(repositories, "Repositories must not be null");
        this.repositories = repositories;
    }

    @Override
    public void handle(final LoginContext context) {
        for (LoginActivityRepository repository : repositories) {
            repository.save(context);
        }
    }
}

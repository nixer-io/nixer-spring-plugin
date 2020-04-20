package io.nixer.nixerplugin.core.login;

import java.util.List;

public class LoginActivityStorage implements LoginActivityHandler {

    private final List<LoginActivityRepository> repositories;

    public LoginActivityStorage(final List<LoginActivityRepository> repositories) {
        this.repositories = repositories;
    }

    @Override
    public void handle(final LoginContext context) {
        for (LoginActivityRepository repository : repositories) {
            repository.save(context);
        }
    }
}

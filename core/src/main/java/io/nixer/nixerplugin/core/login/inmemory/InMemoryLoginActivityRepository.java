package io.nixer.nixerplugin.core.login.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.nixer.nixerplugin.core.login.LoginActivityRepository;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;

/**
 * Stores login results in memory.
 */
public class InMemoryLoginActivityRepository implements LoginActivityRepository, CounterRegistry {

    private final List<LoginCounter> counters = new ArrayList<>();

    @Override
    public void save(final LoginResult result, final LoginContext context) {
        counters.forEach(counter -> counter.onLogin(result, context));
    }

    @Override
    public void registerCounter(LoginCounter loginCounter) {
        counters.add(loginCounter);
    }

}

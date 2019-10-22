package eu.xword.nixer.nixerplugin.core.login.inmemory;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.core.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginMetricCounter;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;

/**
 * Stores login results in memory.
 */
public class InMemoryLoginActivityRepository implements LoginActivityRepository, CounterRegistry {

    private final List<LoginMetricCounter> counters = new ArrayList<>();

    @Override
    public void save(final LoginResult result, final LoginContext context) {
        counters.forEach(counter -> counter.onLogin(result, context));
    }

    @Override
    public void registerCounter(LoginMetricCounter loginMetricCounter) {
        counters.add(loginMetricCounter);
    }

}

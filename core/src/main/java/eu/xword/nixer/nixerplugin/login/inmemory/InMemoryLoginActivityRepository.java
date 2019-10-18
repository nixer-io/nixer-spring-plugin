package eu.xword.nixer.nixerplugin.login.inmemory;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginMetricCounter;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.springframework.stereotype.Repository;

/**
 * Stores login results in memory.
 */
@Repository
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

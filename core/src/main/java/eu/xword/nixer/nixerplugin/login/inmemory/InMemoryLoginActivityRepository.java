package eu.xword.nixer.nixerplugin.login.inmemory;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.counts.LoginCounter;
import org.springframework.stereotype.Repository;

/**
 * Stores user login results in memory.
 */
@Repository
public class InMemoryLoginActivityRepository implements LoginActivityRepository, CounterRegistry {

    private final List<LoginCounter> counters = new ArrayList<>();

    @Override
    public void reportLoginActivity(final LoginResult result, final LoginContext context) {

        counters.forEach(counter -> counter.onLogin(result, context));
    }

    @Override
    public void registerCounter(LoginCounter loginCounter) {
        counters.add(loginCounter);
    }

}

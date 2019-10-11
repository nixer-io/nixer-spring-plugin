package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.counts.LoginMetric;
import eu.xword.nixer.nixerplugin.login.counts.LoginCounter;
import org.springframework.util.Assert;

/**
 * This counter tracks consecutive failed login per ip. Successful login resets counter.
 */
public class LoginMetricCounter implements LoginMetric, LoginCounter {

    private final RollingCounter counter;
    private final FeatureKey featureKey;

    //todo extract filter
    LoginMetricCounter(final LoginMetricCounterBuilder builder) {
        Assert.notNull(builder, "Builder must not be null");

        Assert.notNull(builder.windowSize, "WindowSize must not be null");
        this.counter = new RollingCounter(builder.windowSize);
        this.counter.setClock(builder.clock);

        Assert.notNull(builder.featureKey, "FeatureKey must not be null");
        this.featureKey = builder.featureKey;
    }

    @Override
    public int value(final String key) {
        return counter.get(key);
    }

    @Override
    public void onLogin(final LoginResult result, final LoginContext context) {
        //todo handle nulls
        final String key = this.featureKey.key(context);

        result
                .onSuccess(it -> counter.remove(key))
                .onFailure(it -> counter.add(key));
    }
}

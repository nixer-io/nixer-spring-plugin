package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.counts.LoginCounter;
import eu.xword.nixer.nixerplugin.login.counts.LoginMetric;
import org.springframework.util.Assert;

/**
 * This counter tracks consecutive failed login per ip. Successful login resets counter.
 */
public class LoginMetricCounter implements LoginMetric, LoginCounter {

    private final RollingCounter counter;
    private final FeatureKey featureKey;
    private final CountingStrategy countingStrategy;

    //todo extract filter
    LoginMetricCounter(final LoginMetricCounterBuilder builder) {
        Assert.notNull(builder, "Builder must not be null");

        Assert.notNull(builder.windowSize, "WindowSize must not be null");
        CachedBackedRollingCounter counter = new CachedBackedRollingCounter(builder.windowSize);
        counter.setClock(builder.clock);
        this.counter = counter;

        Assert.notNull(builder.featureKey, "FeatureKey must not be null");
        this.featureKey = builder.featureKey;

        Assert.notNull(builder.countingStrategy, "CountingStrategy must not be null");
        this.countingStrategy = builder.countingStrategy;
    }

    @Override
    public int value(final String key) {
        return key != null ? counter.get(key) : 0;
    }

    @Override
    public void onLogin(final LoginResult result, final LoginContext context) {
        final String key = this.featureKey.key(context);
        if (key != null) {
            countingStrategy.count(counter, result).accept(key);
        }
    }
}

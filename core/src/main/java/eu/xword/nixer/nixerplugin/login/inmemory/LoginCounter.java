package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.LoginMetricCounter;
import org.springframework.util.Assert;

/**
 * This counter tracks counts for login.
 */
public class LoginCounter implements LoginMetric, LoginMetricCounter {

    private final RollingCounter counter;
    private final FeatureKey featureKey;
    private final CountingStrategy countingStrategy;

    //todo extract filter that will eg. filter particular types of failures
    LoginCounter(final LoginCounterBuilder builder) {
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
            countingStrategy.counter(counter, result).accept(key);
        }
    }
}

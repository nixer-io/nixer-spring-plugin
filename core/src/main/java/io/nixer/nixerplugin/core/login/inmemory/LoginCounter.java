package io.nixer.nixerplugin.core.login.inmemory;

import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import org.springframework.util.Assert;

/**
 * This counter tracks counts for login.
 */
public class LoginCounter implements LoginMetric {

    private final RollingCounter counter;
    private final FeatureKey featureKey;
    private final CountingStrategy countingStrategy;

    LoginCounter(final RollingCounter counter,
                 final FeatureKey featureKey,
                 final CountingStrategy countingStrategy) {
        Assert.notNull(counter, "FeatureKey must not be null");
        Assert.notNull(featureKey, "FeatureKey must not be null");
        Assert.notNull(countingStrategy, "CountingStrategy must not be null");

        this.counter = counter;
        this.featureKey = featureKey;
        this.countingStrategy = countingStrategy;
    }

    @Override
    public int value(final String key) {
        return key != null ? counter.count(key) : 0;
    }

    public void onLogin(final LoginResult result, final LoginContext context) {
        final String key = this.featureKey.key(context);
        if (key != null) {
            countingStrategy.counter(counter, result).accept(key);
        }
    }
}

package io.nixer.nixerplugin.core.login.inmemory;

import io.nixer.nixerplugin.core.login.LoginContext;
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
        Assert.notNull(counter, "counter must not be null");
        Assert.notNull(featureKey, "featureKey must not be null");
        Assert.notNull(countingStrategy, "countingStrategy must not be null");

        this.counter = counter;
        this.featureKey = featureKey;
        this.countingStrategy = countingStrategy;
    }

    @Override
    public int value(final String key) {
        return key != null ? counter.count(key) : 0;
    }

    public void onLogin(final LoginContext context) {
        final String key = this.featureKey.key(context);
        if (key != null) {
            countingStrategy.counter(counter, context.getLoginResult()).accept(key);
        }
    }
}

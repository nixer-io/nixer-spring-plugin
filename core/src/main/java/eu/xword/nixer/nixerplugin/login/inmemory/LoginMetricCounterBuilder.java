package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;

import eu.xword.nixer.nixerplugin.detection.config.WindowSize;
import org.springframework.util.Assert;

/**
 * Builder class for {@link LoginMetricCounter}
 */
public class LoginMetricCounterBuilder {

    FeatureKey featureKey;
    Clock clock = Clock.systemDefaultZone();
    Duration windowSize = WindowSize.WINDOW_5M;
    CountingStrategy countingStrategy = CountingStrategies.CONSECUTIVE_FAILS;

    private LoginMetricCounterBuilder() {
    }

    public static LoginMetricCounterBuilder counter(final FeatureKey key) {
        Assert.notNull(key, "FeatureKey must not be null");

        final LoginMetricCounterBuilder builder = new LoginMetricCounterBuilder();
        builder.featureKey = key;
        return builder;
    }

    public LoginMetricCounterBuilder clock(final Clock clock) {
        Assert.notNull(clock, "Clock must not be null");
        this.clock = clock;
        return this;
    }

    public LoginMetricCounterBuilder window(final Duration windowSize) {
        Assert.notNull(windowSize, "WindowSize must not be null");
        this.windowSize = windowSize;
        return this;
    }

    public LoginMetricCounterBuilder count(final CountingStrategy countingStrategy) {
        Assert.notNull(countingStrategy, "CountingStrategy must not be null");
        this.countingStrategy = countingStrategy;
        return this;
    }


    public LoginMetricCounter build() {
        return new LoginMetricCounter(this);
    }
}

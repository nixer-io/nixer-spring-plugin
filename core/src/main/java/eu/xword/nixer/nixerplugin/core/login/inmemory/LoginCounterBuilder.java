package eu.xword.nixer.nixerplugin.core.login.inmemory;

import java.time.Clock;
import java.time.Duration;

import eu.xword.nixer.nixerplugin.core.detection.config.WindowSize;
import org.springframework.util.Assert;

/**
 * Builder class for {@link LoginCounter}
 */
public class LoginCounterBuilder {

    FeatureKey featureKey;
    Clock clock = Clock.systemDefaultZone();
    Duration windowSize = WindowSize.WINDOW_5M;
    CountingStrategy countingStrategy = CountingStrategies.CONSECUTIVE_FAILS;

    private LoginCounterBuilder() {
    }

    public static LoginCounterBuilder counter(final FeatureKey key) {
        Assert.notNull(key, "FeatureKey must not be null");

        final LoginCounterBuilder builder = new LoginCounterBuilder();
        builder.featureKey = key;
        return builder;
    }

    public LoginCounterBuilder clock(final Clock clock) {
        Assert.notNull(clock, "Clock must not be null");
        this.clock = clock;
        return this;
    }

    public LoginCounterBuilder window(final Duration windowSize) {
        Assert.notNull(windowSize, "WindowSize must not be null");
        WindowSize.validate(windowSize);
        this.windowSize = windowSize;
        return this;
    }

    public LoginCounterBuilder count(final CountingStrategy countingStrategy) {
        Assert.notNull(countingStrategy, "CountingStrategy must not be null");
        this.countingStrategy = countingStrategy;
        return this;
    }


    public LoginCounter build() {
        return new LoginCounter(this);
    }
}

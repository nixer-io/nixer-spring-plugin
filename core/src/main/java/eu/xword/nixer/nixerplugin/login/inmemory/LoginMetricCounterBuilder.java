package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;

import eu.xword.nixer.nixerplugin.rules.WindowSize;
import org.springframework.util.Assert;

/**
 * Builder class for {@link LoginMetricCounter}
 */
public class LoginMetricCounterBuilder {

    FeatureKey featureKey;
    Clock clock = Clock.systemDefaultZone();
    Duration windowSize = WindowSize.WINDOW_5M;

    private LoginMetricCounterBuilder() {
    }

    public static LoginMetricCounterBuilder counter() {
        return new LoginMetricCounterBuilder();
    }

    public LoginMetricCounterBuilder key(final FeatureKey key) {
        Assert.notNull(key, "Key must not be null");
        this.featureKey = key;
        return this;
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


    public LoginMetricCounter build() {
        return new LoginMetricCounter(this);
    }
}

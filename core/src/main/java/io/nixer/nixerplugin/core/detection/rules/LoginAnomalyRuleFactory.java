package io.nixer.nixerplugin.core.detection.rules;

import java.time.Duration;

import io.nixer.nixerplugin.core.detection.rules.ratio.FailedLoginRatioRule;
import io.nixer.nixerplugin.core.fingerprint.loginThreshold.FingerprintFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.IpFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UserAgentFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UsernameFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.CountingStrategies;
import io.nixer.nixerplugin.core.login.inmemory.FeatureKey;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounter;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounterBuilder;
import org.springframework.util.Assert;

public class LoginAnomalyRuleFactory {

    private final CounterRegistry counterRegistry;

    public LoginAnomalyRuleFactory(final CounterRegistry counterRegistry) {
        Assert.notNull(counterRegistry, "CounterRegistry must not be null");
        this.counterRegistry = counterRegistry;
    }

    public FailedLoginRatioRule createFailedLoginRatioRule(final Duration window,
                                                           final Integer activationLevel,
                                                           final Integer deactivationLevel,
                                                           final Integer minimumSampleSize) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.LOGIN_STATUS)
                .window(window)
                .count(CountingStrategies.ALL)
                .buildCachedRollingCounter();
        counterRegistry.registerCounter(counter);

        return new FailedLoginRatioRule(counter, activationLevel, deactivationLevel, minimumSampleSize);
    }

    public UsernameFailedLoginOverThresholdRule createUsernameRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.USERNAME)
                .window(window)
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .buildCachedRollingCounter();
        counterRegistry.registerCounter(counter);


        final UsernameFailedLoginOverThresholdRule rule = new UsernameFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }

    public UserAgentFailedLoginOverThresholdRule createUserAgentRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.USER_AGENT_TOKEN)
                .window(window)
                .count(CountingStrategies.TOTAL_FAILS)
                .buildCachedRollingCounter();
        counterRegistry.registerCounter(counter);

        final UserAgentFailedLoginOverThresholdRule rule = new UserAgentFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }

    public IpFailedLoginOverThresholdRule createIpRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.IP)
                .window(window)
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .buildCachedRollingCounter();
        counterRegistry.registerCounter(counter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }

    public FingerprintFailedLoginOverThresholdRule createFingerprintRule(final Duration window, final Integer threshold) {

        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.FINGERPRINT)
                .window(window)
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .buildCachedRollingCounter();

        counterRegistry.registerCounter(counter);

        final FingerprintFailedLoginOverThresholdRule rule = new FingerprintFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }
}

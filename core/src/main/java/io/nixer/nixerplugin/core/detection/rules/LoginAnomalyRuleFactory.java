package io.nixer.nixerplugin.core.detection.rules;

import java.time.Duration;

import io.nixer.nixerplugin.core.detection.rules.threshold.IpFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UserAgentLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UsernameFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.CountingStrategies;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounter;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounterBuilder;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.CountingStrategies;
import io.nixer.nixerplugin.core.login.inmemory.FeatureKey;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounter;
import io.nixer.nixerplugin.core.login.inmemory.LoginCounterBuilder;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.IP;
import static io.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.USERNAME;
import static io.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.USER_AGENT_TOKEN;

public class LoginAnomalyRuleFactory {

    private final CounterRegistry counterRegistry;

    public LoginAnomalyRuleFactory(final CounterRegistry counterRegistry) {
        Assert.notNull(counterRegistry, "CounterRegistry must not be null");
        this.counterRegistry = counterRegistry;
    }

    public UsernameFailedLoginOverThresholdRule createUsernameRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.USERNAME)
                .window(window)
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);


        final UsernameFailedLoginOverThresholdRule rule = new UsernameFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }

    public UserAgentLoginOverThresholdRule createUserAgentRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.USER_AGENT_TOKEN)
                .window(window)
                .count(CountingStrategies.TOTAL_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final UserAgentLoginOverThresholdRule rule = new UserAgentLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }

    public IpFailedLoginOverThresholdRule createIpRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.IP)
                .window(window)
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(counter);
        if (threshold != null) {
            rule.setThreshold(threshold);
        }
        return rule;
    }
}

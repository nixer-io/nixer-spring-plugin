package eu.xword.nixer.nixerplugin.core.detection;

import java.time.Duration;

import eu.xword.nixer.nixerplugin.core.detection.rules.IpFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.UserAgentLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.UsernameFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.core.login.inmemory.CountingStrategies;
import eu.xword.nixer.nixerplugin.core.login.inmemory.LoginCounter;
import eu.xword.nixer.nixerplugin.core.login.inmemory.LoginCounterBuilder;

import static eu.xword.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.IP;
import static eu.xword.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.USERNAME;
import static eu.xword.nixer.nixerplugin.core.login.inmemory.FeatureKey.Features.USER_AGENT_TOKEN;

class LoginThresholdRuleFactory {

    private final CounterRegistry counterRegistry;

    public LoginThresholdRuleFactory(final CounterRegistry counterRegistry) {
        this.counterRegistry = counterRegistry;
    }

    UsernameFailedLoginOverThresholdRule createUsernameRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(USERNAME)
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

    UserAgentLoginOverThresholdRule createUserAgentRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(USER_AGENT_TOKEN)
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

    IpFailedLoginOverThresholdRule createIpRule(final Duration window, final Integer threshold) {
        final LoginCounter counter = LoginCounterBuilder.counter(IP)
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

package eu.xword.nixer.nixerplugin.rules;

import java.time.Duration;

import eu.xword.nixer.nixerplugin.login.inmemory.ConsecutiveFailedLoginCounter;
import eu.xword.nixer.nixerplugin.login.inmemory.CounterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IpFailedLoginThresholdRuleProperties.class)
public class RuleConfiguration {

    @Bean
    public RulesEngine rulesEngine(ApplicationEventPublisher eventPublisher) {
        return new RulesEngine(eventPublisher);
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.ip-failed-login-threshold", name = "enabled", havingValue = "true")
    public IpFailedLoginOverThresholdRule failedLoginThresholdRule(CounterRegistry counterRegistry, RulesEngine rulesEngine, IpFailedLoginThresholdRuleProperties ruleProperties) {
        final Duration duration = ruleProperties.getWindow();
        final ConsecutiveFailedLoginCounter loginCounter = ConsecutiveFailedLoginCounter.create(duration);
        counterRegistry.registerCounter(loginCounter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(loginCounter);
        rule.setThreshold(ruleProperties.getThreshold());

        rulesEngine.addRule(rule);

        return rule;
    }
}

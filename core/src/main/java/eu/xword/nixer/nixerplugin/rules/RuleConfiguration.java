package eu.xword.nixer.nixerplugin.rules;

import java.util.List;

import eu.xword.nixer.nixerplugin.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetricCounter;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetricCounterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.IP;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.USERNAME;

@Configuration
@EnableConfigurationProperties(FailedLoginThresholdRulesProperties.class)
public class RuleConfiguration {

    private CounterRegistry counterRegistry;

    public RuleConfiguration(final CounterRegistry counterRegistry) {
        this.counterRegistry = counterRegistry;
    }

    @Bean
    public RulesRunner rulesEngine(ApplicationEventPublisher eventPublisher, @Autowired(required = false) List<Rule> rules) {
        final RulesRunner rulesRunner = new RulesRunner(eventPublisher);

        rules.forEach(rulesRunner::addRule);

        return rulesRunner;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.ip", name = "enabled", havingValue = "true")
    public IpFailedLoginOverThresholdRule ipFailedLoginThresholdRule(FailedLoginThresholdRulesProperties ruleProperties) {
        final RuleProperties properties = ruleProperties.getFailedLoginThreshold().get("ip");

        final LoginMetricCounter counter = LoginMetricCounterBuilder.counter()
                .window(properties.getWindow())
                .key(IP)
                .build();
        counterRegistry.registerCounter(counter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(counter);
        rule.setThreshold(properties.getThreshold());

        return rule;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.username", name = "enabled", havingValue = "true")
    public UsernameFailedLoginOverThresholdRule usernameFailedLoginThresholdRule(FailedLoginThresholdRulesProperties ruleProperties) {
        final RuleProperties properties = ruleProperties.getFailedLoginThreshold().get("username");

        final LoginMetricCounter counter = LoginMetricCounterBuilder.counter()
                .window(properties.getWindow())
                .key(USERNAME)
                .build();
        counterRegistry.registerCounter(counter);

        final UsernameFailedLoginOverThresholdRule rule = new UsernameFailedLoginOverThresholdRule(counter);
        rule.setThreshold(properties.getThreshold());

        return rule;
    }

}

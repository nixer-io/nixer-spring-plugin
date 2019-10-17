package eu.xword.nixer.nixerplugin.detection.config;

import java.util.List;

import eu.xword.nixer.nixerplugin.detection.rules.IpFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.detection.rules.Rule;
import eu.xword.nixer.nixerplugin.detection.rules.RulesRunner;
import eu.xword.nixer.nixerplugin.detection.rules.UserAgentLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.detection.rules.UsernameFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.login.inmemory.CountingStrategies;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetricCounter;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetricCounterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.detection.config.FailedLoginThresholdRulesProperties.Name.ip;
import static eu.xword.nixer.nixerplugin.detection.config.FailedLoginThresholdRulesProperties.Name.useragent;
import static eu.xword.nixer.nixerplugin.detection.config.FailedLoginThresholdRulesProperties.Name.username;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.IP;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.USERNAME;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.USER_AGENT_TOKEN;

@Configuration
@EnableConfigurationProperties(FailedLoginThresholdRulesProperties.class)
public class DetectionConfiguration {

    private final CounterRegistry counterRegistry;

    public DetectionConfiguration(final CounterRegistry counterRegistry) {
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
        final RuleProperties properties = ruleProperties.getFailedLoginThreshold().get(ip);

        final LoginMetricCounter counter = LoginMetricCounterBuilder.counter(IP)
                .window(properties.getWindow())
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(counter);
        rule.setThreshold(properties.getThreshold());

        return rule;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.username", name = "enabled", havingValue = "true")
    public UsernameFailedLoginOverThresholdRule usernameFailedLoginThresholdRule(FailedLoginThresholdRulesProperties ruleProperties) {
        final RuleProperties properties = ruleProperties.getFailedLoginThreshold().get(username);

        final LoginMetricCounter counter = LoginMetricCounterBuilder.counter(USERNAME)
                .window(properties.getWindow())
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final UsernameFailedLoginOverThresholdRule rule = new UsernameFailedLoginOverThresholdRule(counter);
        rule.setThreshold(properties.getThreshold());

        return rule;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.useragent", name = "enabled", havingValue = "true")
    public UserAgentLoginOverThresholdRule userAgentFailedLoginThresholdRule(FailedLoginThresholdRulesProperties ruleProperties) {
        final RuleProperties properties = ruleProperties.getFailedLoginThreshold().get(useragent);

        final LoginMetricCounter counter = LoginMetricCounterBuilder.counter(USER_AGENT_TOKEN)
                .window(properties.getWindow())
                .count(CountingStrategies.TOTAL_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final UserAgentLoginOverThresholdRule rule = new UserAgentLoginOverThresholdRule(counter);
        rule.setThreshold(properties.getThreshold());

        return rule;
    }
}

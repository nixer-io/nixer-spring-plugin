package eu.xword.nixer.nixerplugin.detection.config;

import java.util.List;
import java.util.Optional;

import eu.xword.nixer.nixerplugin.detection.rules.AnomalyRule;
import eu.xword.nixer.nixerplugin.detection.rules.AnomalyRulesRunner;
import eu.xword.nixer.nixerplugin.detection.rules.IpFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.detection.rules.UserAgentLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.detection.rules.UsernameFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.login.inmemory.CountingStrategies;
import eu.xword.nixer.nixerplugin.login.inmemory.InMemoryLoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginCounter;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginCounterBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties.Name.ip;
import static eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties.Name.useragent;
import static eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties.Name.username;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.IP;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.USERNAME;
import static eu.xword.nixer.nixerplugin.login.inmemory.FeatureKey.Features.USER_AGENT_TOKEN;

@Configuration
@EnableConfigurationProperties(AnomalyRulesProperties.class)
public class DetectionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InMemoryLoginActivityRepository counterRegistry() {
        return new InMemoryLoginActivityRepository();
    }

    @Bean
    public AnomalyRulesRunner rulesEngine(ApplicationEventPublisher eventPublisher, List<AnomalyRule> anomalyRules) {
        final AnomalyRulesRunner anomalyRulesRunner = new AnomalyRulesRunner(eventPublisher);

        anomalyRules.forEach(anomalyRulesRunner::addRule);

        return anomalyRulesRunner;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.ip", name = "enabled", havingValue = "true")
    public IpFailedLoginOverThresholdRule ipFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties, CounterRegistry counterRegistry) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(ip);

        final LoginCounter counter = LoginCounterBuilder.counter(IP)
                .window(properties.getWindow())
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final IpFailedLoginOverThresholdRule rule = new IpFailedLoginOverThresholdRule(counter);
        Optional.ofNullable(properties.getThreshold())
                .ifPresent(rule::setThreshold);

        return rule;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.username", name = "enabled", havingValue = "true")
    public UsernameFailedLoginOverThresholdRule usernameFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties, CounterRegistry counterRegistry) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(username);

        final LoginCounter counter = LoginCounterBuilder.counter(USERNAME)
                .window(properties.getWindow())
                .count(CountingStrategies.CONSECUTIVE_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final UsernameFailedLoginOverThresholdRule rule = new UsernameFailedLoginOverThresholdRule(counter);
        Optional.ofNullable(properties.getThreshold())
                .ifPresent(rule::setThreshold);

        return rule;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.useragent", name = "enabled", havingValue = "true")
    public UserAgentLoginOverThresholdRule userAgentFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties, CounterRegistry counterRegistry) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(useragent);

        final LoginCounter counter = LoginCounterBuilder.counter(USER_AGENT_TOKEN)
                .window(properties.getWindow())
                .count(CountingStrategies.TOTAL_FAILS)
                .build();
        counterRegistry.registerCounter(counter);

        final UserAgentLoginOverThresholdRule rule = new UserAgentLoginOverThresholdRule(counter);
        Optional.ofNullable(properties.getThreshold())
                .ifPresent(rule::setThreshold);

        return rule;
    }
}

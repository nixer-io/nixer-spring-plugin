package eu.xword.nixer.nixerplugin.core.detection;

import java.util.List;

import eu.xword.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import eu.xword.nixer.nixerplugin.core.detection.config.WindowThresholdRuleProperties;
import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import eu.xword.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.core.login.inmemory.InMemoryLoginActivityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.ip;
import static eu.xword.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.useragent;
import static eu.xword.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.username;

@Configuration
@EnableConfigurationProperties(AnomalyRulesProperties.class)
public class DetectionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CounterRegistry counterRegistry() {
        return new InMemoryLoginActivityRepository();
    }

    @Bean
    public AnomalyRulesRunner rulesEngine(ApplicationEventPublisher eventPublisher, List<AnomalyRule> anomalyRules) {

        return new AnomalyRulesRunner(eventPublisher, anomalyRules);
    }

    @Bean
    public LoginThresholdRuleFactory ruleFactory() {
        return new LoginThresholdRuleFactory(counterRegistry());
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.ip", name = "enabled", havingValue = "true")
    public AnomalyRule ipFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(ip);

        return ruleFactory().createIpRule(properties.getWindow(), properties.getThreshold());
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.username", name = "enabled", havingValue = "true")
    public AnomalyRule usernameFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(username);

        return ruleFactory().createUsernameRule(properties.getWindow(), properties.getThreshold());
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.useragent", name = "enabled", havingValue = "true")
    public AnomalyRule userAgentFailedLoginThresholdRule(AnomalyRulesProperties ruleProperties) {
        final WindowThresholdRuleProperties properties = ruleProperties.getFailedLoginThreshold().get(useragent);

        return ruleFactory().createUserAgentRule(properties.getWindow(), properties.getThreshold());
    }
}

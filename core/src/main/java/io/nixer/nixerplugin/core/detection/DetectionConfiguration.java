package io.nixer.nixerplugin.core.detection;

import java.util.List;

import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import io.nixer.nixerplugin.core.detection.config.WindowThresholdRuleProperties;
import io.nixer.nixerplugin.core.detection.filter.login.IpFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UserAgentFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UsernameFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UserAgentOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UsernameOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.rules.AnomalyRule;
import io.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import io.nixer.nixerplugin.core.detection.rules.LoginAnomalyRuleFactory;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.InMemoryLoginActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(AnomalyRulesProperties.class)
@Import({
        DetectionConfiguration.IpThresholdRule.class,
        DetectionConfiguration.UsernameThresholdRule.class,
        DetectionConfiguration.UserAgentThresholdRule.class
})
public class DetectionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CounterRegistry counterRegistry() {
        return new InMemoryLoginActivityRepository();
    }

    @Bean
    public LoginAnomalyRuleFactory ruleFactory() {
        return new LoginAnomalyRuleFactory(counterRegistry());
    }

    @Bean
    public AnomalyRulesRunner rulesEngine(ApplicationEventPublisher eventPublisher, List<AnomalyRule> anomalyRules) {

        return new AnomalyRulesRunner(eventPublisher, anomalyRules);
    }

    /*
    Ideally we would set bean name prefix for beans inside nested configuration classes.

    Apparently spring doesn't support that.
    */
    @Configuration
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.ip", name = "enabled", havingValue = "true")
    static class IpThresholdRule {

        @Autowired
        DetectionConfiguration detection;

        @Bean
        @ConfigurationProperties(prefix = "nixer.rules.failed-login-threshold.ip")
        public WindowThresholdRuleProperties ipThresholdRulesProperties() {
            return new WindowThresholdRuleProperties();
        }

        @Bean
        public AnomalyRule ipFailedLoginThresholdRule() {
            final WindowThresholdRuleProperties properties = ipThresholdRulesProperties();

            return detection.ruleFactory()
                    .createIpRule(properties.getWindow(), properties.getThreshold());
        }

        @Bean
        public IpFailedLoginOverThresholdFilter ipFilter() {
            return new IpFailedLoginOverThresholdFilter(ipRegistry());
        }

        @Bean
        public IpOverLoginThresholdRegistry ipRegistry() {
            return new IpOverLoginThresholdRegistry();
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.username", name = "enabled", havingValue = "true")
    static class UsernameThresholdRule {

        @Autowired
        DetectionConfiguration detection;

        @Bean
        @ConfigurationProperties(prefix = "nixer.rules.failed-login-threshold.username")
        public WindowThresholdRuleProperties usernameThresholdRulesProperties() {
            return new WindowThresholdRuleProperties();
        }

        @Bean
        public AnomalyRule usernameFailedLoginThresholdRule() {
            final WindowThresholdRuleProperties properties = usernameThresholdRulesProperties();

            return detection.ruleFactory()
                    .createUsernameRule(properties.getWindow(), properties.getThreshold());
        }

        @Bean
        public UsernameFailedLoginOverThresholdFilter usernameFilter() {
            return new UsernameFailedLoginOverThresholdFilter(usernameRegistry());
        }

        @Bean
        public UsernameOverLoginThresholdRegistry usernameRegistry() {
            return new UsernameOverLoginThresholdRegistry();
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-threshold.useragent", name = "enabled", havingValue = "true")
    static class UserAgentThresholdRule {

        @Autowired
        DetectionConfiguration detection;

        @Bean
        @ConfigurationProperties(prefix = "nixer.rules.failed-login-threshold.useragent")
        public WindowThresholdRuleProperties userAgentThresholdRulesProperties() {
            return new WindowThresholdRuleProperties();
        }

        @Bean
        public AnomalyRule userAgentFailedLoginThresholdRule() {
            final WindowThresholdRuleProperties properties = userAgentThresholdRulesProperties();

            return detection.ruleFactory()
                    .createUserAgentRule(properties.getWindow(), properties.getThreshold());
        }

        @Bean
        public UserAgentFailedLoginOverThresholdFilter userAgentFilter() {
            return new UserAgentFailedLoginOverThresholdFilter(userAgentRegistry());
        }

        @Bean
        public UserAgentOverLoginThresholdRegistry userAgentRegistry() {
            return new UserAgentOverLoginThresholdRegistry();
        }
    }

}

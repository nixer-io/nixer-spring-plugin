package io.nixer.nixerplugin.core.detection;

import java.util.List;

import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import io.nixer.nixerplugin.core.detection.config.FailedLoginRatioProperties;
import io.nixer.nixerplugin.core.detection.config.WindowThresholdRuleProperties;
import io.nixer.nixerplugin.core.detection.filter.login.FailedLoginRatioFilter;
import io.nixer.nixerplugin.core.detection.filter.login.IpFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UserAgentFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UsernameFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.registry.FailedLoginRatioRegistry;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UserAgentOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UsernameOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.rules.LoginAnomalyRuleFactory;
import io.nixer.nixerplugin.core.detection.rules.LoginRule;
import io.nixer.nixerplugin.core.detection.rules.RulesRunner;
import io.nixer.nixerplugin.core.domain.useragent.UserAgentTokenizer;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.InMemoryLoginActivityRepository;
import io.nixer.nixerplugin.core.util.NowSource;
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
        DetectionConfiguration.UserAgentThresholdRule.class,
        DetectionConfiguration.FailedLoginRatioRule.class
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
    public RulesRunner rulesEngine(ApplicationEventPublisher eventPublisher, List<LoginRule> loginRules) {

        return new RulesRunner(eventPublisher, loginRules);
    }

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
        public LoginRule ipFailedLoginThresholdRule() {
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
        public LoginRule usernameFailedLoginThresholdRule() {
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
        public LoginRule userAgentFailedLoginThresholdRule() {
            final WindowThresholdRuleProperties properties = userAgentThresholdRulesProperties();

            return detection.ruleFactory()
                    .createUserAgentRule(properties.getWindow(), properties.getThreshold());
        }

        @Bean
        public UserAgentFailedLoginOverThresholdFilter userAgentFilter() {
            return new UserAgentFailedLoginOverThresholdFilter(userAgentTokenizer(), userAgentRegistry());
        }

        @Bean
        public UserAgentTokenizer userAgentTokenizer() {
            return UserAgentTokenizer.sha1Tokenizer();
        }

        @Bean
        public UserAgentOverLoginThresholdRegistry userAgentRegistry() {
            return new UserAgentOverLoginThresholdRegistry();
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "nixer.rules.failed-login-ratio-level", name = "enabled", havingValue = "true")
    static class FailedLoginRatioRule {

        @Autowired
        DetectionConfiguration detection;

        @Autowired
        NowSource nowSource;

        @Bean
        @ConfigurationProperties(prefix = "nixer.rules.failed-login-ratio-level")
        public FailedLoginRatioProperties failedLoginRatioProperties() {
            return new FailedLoginRatioProperties();
        }

        @Bean
        public LoginRule failedLoginRatioRule() {
            final FailedLoginRatioProperties properties = failedLoginRatioProperties();

            return detection.ruleFactory().createFailedLoginRatioRule(
                    properties.getWindow(),
                    properties.getActivationLevel(),
                    properties.getDeactivationLevel(),
                    properties.getMinimumSampleSize());
        }

        @Bean
        public FailedLoginRatioFilter failedLoginRatioFilter() {
            return new FailedLoginRatioFilter(failedLoginRatioRegistry());
        }

        @Bean
        public FailedLoginRatioRegistry failedLoginRatioRegistry() {
            return new FailedLoginRatioRegistry(nowSource);
        }
    }

}

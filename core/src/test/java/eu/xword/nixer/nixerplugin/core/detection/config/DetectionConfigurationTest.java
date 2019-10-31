package eu.xword.nixer.nixerplugin.core.detection.config;

import eu.xword.nixer.nixerplugin.core.detection.DetectionConfiguration;
import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import eu.xword.nixer.nixerplugin.core.detection.rules.IpFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.UserAgentLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.detection.rules.UsernameFailedLoginOverThresholdRule;
import eu.xword.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import eu.xword.nixer.nixerplugin.core.login.inmemory.InMemoryLoginActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class DetectionConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Configuration
    public static class TestCounterRegistryConfiguration {

        private static final CounterRegistry CUSTOM_COUNTER_REGISTRY = (it) -> {
        };

        @Bean
        CounterRegistry counterRegistry() {
            return CUSTOM_COUNTER_REGISTRY;
        }
    }

    @Configuration
    public static class CustomRuleConfiguration {

        private static final AnomalyRule CUSTOM_RULE = (loginContext, eventEmitter) -> {
        };

        @Bean
        AnomalyRule customAnomalyRule() {
            return CUSTOM_RULE;
        }
    }

    @Test
    void shouldRegisterEnabledRules() {
        contextRunner
                .withPropertyValues(
                        "nixer.rules.failed-login-threshold.ip.enabled=true",
                        "nixer.rules.failed-login-threshold.username.enabled=true",
                        "nixer.rules.failed-login-threshold.useragent.enabled=true"
                )
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AnomalyRulesRunner.class);
                    assertThat(context).hasSingleBean(IpFailedLoginOverThresholdRule.class);
                    assertThat(context).hasSingleBean(UserAgentLoginOverThresholdRule.class);
                    assertThat(context).hasSingleBean(UsernameFailedLoginOverThresholdRule.class);
                    assertThat(context).getBeanNames(AnomalyRule.class).hasSize(3);
                });
    }

    @Test
    void shouldRegisterCustomRule() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class, CustomRuleConfiguration.class)
                .run(context -> {
                    assertThat(context).getBeanNames(AnomalyRule.class).hasSize(1);
                    assertThat(context).getBean(AnomalyRule.class).isSameAs(CustomRuleConfiguration.CUSTOM_RULE);
                });
    }

    @Test
    void shouldNotRegisterDisabledRules() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AnomalyRulesRunner.class);
                    assertThat(context).getBeanNames(AnomalyRule.class).isEmpty();
                });
    }

    @Test
    void shouldRegisterDefaultCounterRegistry() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> assertThat(context).hasSingleBean(InMemoryLoginActivityRepository.class));
    }

    @Test
    void shouldUseCustomCounterRegistry() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class, TestCounterRegistryConfiguration.class)
                .run(context -> assertThat(context).getBean(CounterRegistry.class)
                        .isSameAs(TestCounterRegistryConfiguration.CUSTOM_COUNTER_REGISTRY));
    }
}
package io.nixer.nixerplugin.core.detection.config;

import io.nixer.nixerplugin.core.detection.DetectionConfiguration;
import io.nixer.nixerplugin.core.detection.filter.login.IpFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UserAgentFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.filter.login.UsernameFailedLoginOverThresholdFilter;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UserAgentOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.registry.UsernameOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.rules.LoginRule;
import io.nixer.nixerplugin.core.detection.rules.RulesRunner;
import io.nixer.nixerplugin.core.detection.rules.threshold.IpFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UserAgentFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.detection.rules.threshold.UsernameFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.login.inmemory.CounterRegistry;
import io.nixer.nixerplugin.core.login.inmemory.InMemoryLoginActivityRepository;
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

        private static final LoginRule CUSTOM_RULE = (loginContext, eventEmitter) -> {
        };

        @Bean
        LoginRule customAnomalyRule() {
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
                    assertThat(context).hasSingleBean(RulesRunner.class);
                    assertThat(context).hasSingleBean(IpFailedLoginOverThresholdRule.class);
                    assertThat(context).hasSingleBean(UserAgentFailedLoginOverThresholdRule.class);
                    assertThat(context).hasSingleBean(UsernameFailedLoginOverThresholdRule.class);
                    assertThat(context).getBeanNames(LoginRule.class).hasSize(3);
                });
    }

    @Test
    void shouldRegisterEnabledUsernameRules() {
        contextRunner
                .withPropertyValues(
                        "nixer.rules.failed-login-threshold.username.enabled=true"
                )
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RulesRunner.class);
                    assertThat(context).hasSingleBean(UsernameFailedLoginOverThresholdRule.class);
                    assertThat(context).getBeanNames(LoginRule.class).hasSize(1);
                    assertThat(context).hasSingleBean(UsernameFailedLoginOverThresholdFilter.class);
                    assertThat(context).hasSingleBean(UsernameOverLoginThresholdRegistry.class);
                });
    }


    @Test
    void shouldRegisterEnabledUserAgentRules() {
        contextRunner
                .withPropertyValues(
                        "nixer.rules.failed-login-threshold.useragent.enabled=true"
                )
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RulesRunner.class);
                    assertThat(context).hasSingleBean(UserAgentFailedLoginOverThresholdRule.class);
                    assertThat(context).getBeanNames(LoginRule.class).hasSize(1);
                    assertThat(context).hasSingleBean(UserAgentFailedLoginOverThresholdFilter.class);
                    assertThat(context).hasSingleBean(UserAgentOverLoginThresholdRegistry.class);
                });
    }

    @Test
    void shouldRegisterEnabledIpRules() {
        contextRunner
                .withPropertyValues(
                        "nixer.rules.failed-login-threshold.ip.enabled=true"
                )
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RulesRunner.class);
                    assertThat(context).hasSingleBean(IpFailedLoginOverThresholdRule.class);
                    assertThat(context).getBeanNames(LoginRule.class).hasSize(1);
                    assertThat(context).hasSingleBean(IpFailedLoginOverThresholdFilter.class);
                    assertThat(context).hasSingleBean(IpOverLoginThresholdRegistry.class);
                });
    }

    @Test
    void shouldRegisterCustomRule() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class, CustomRuleConfiguration.class)
                .run(context -> {
                    assertThat(context).getBeanNames(LoginRule.class).hasSize(1);
                    assertThat(context).getBean(LoginRule.class).isSameAs(CustomRuleConfiguration.CUSTOM_RULE);
                });
    }

    @Test
    void shouldNotRegisterDisabledRules() {
        contextRunner
                .withUserConfiguration(DetectionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RulesRunner.class);
                    assertThat(context).getBeanNames(LoginRule.class).isEmpty();
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

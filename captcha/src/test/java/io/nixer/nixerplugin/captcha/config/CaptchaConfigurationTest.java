package io.nixer.nixerplugin.captcha.config;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaClient;
import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaV2ServiceFactory;
import io.nixer.nixerplugin.captcha.security.CaptchaAuthenticationProvider;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import io.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class CaptchaConfigurationTest {

    private static final RestTemplate CUSTOM_TEMPLATE = new RestTemplate();
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Configuration
    public static class TestConfiguration {
        @Bean
        public MetricsFactory metricsFactories() {
            return MetricsFactory.createNullFactory();
        }

        @Bean
        public HttpServletRequest httpServletRequest() {
            return new MockHttpServletRequest();
        }
    }

    @Configuration
    public static class TestConfigurationWithRestTemplate extends TestConfiguration {

        @Bean("recaptchaRestTemplate")
        public RestTemplate customTemplate() {
            return CUSTOM_TEMPLATE;
        }
    }

    @Test
    void shouldRegisterCaptchaBeans() {
        contextRunner
                .withUserConfiguration(
                        TestConfiguration.class,
                        CaptchaConfiguration.class
                )
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(CaptchaAuthenticationProvider.class)
                            .hasSingleBean(CaptchaChecker.class)
                            .hasSingleBean(CaptchaValidator.class)
                            .hasSingleBean(CaptchaEndpoint.class)
                            .hasSingleBean(LoginFailureTypeRegistry.Contributor.class);

                    assertThat(context)
                            .hasSingleBean(RecaptchaV2ServiceFactory.class)
                            .hasSingleBean(RecaptchaClient.class)
                            .hasBean("recaptchaRestTemplate");
                });
    }

    @Test
    void shouldNotRegisterRestTemplateIfOverridden() {
        contextRunner
                .withUserConfiguration(
                        TestConfigurationWithRestTemplate.class,
                        CaptchaConfiguration.class
                )
                .run(context -> {
                    assertThat(context).getBean("recaptchaRestTemplate").isSameAs(CUSTOM_TEMPLATE);
                });
    }

}

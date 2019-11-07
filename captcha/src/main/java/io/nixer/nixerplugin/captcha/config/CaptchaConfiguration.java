package io.nixer.nixerplugin.captcha.config;

import io.nixer.nixerplugin.captcha.CaptchaBehavior;
import io.nixer.nixerplugin.captcha.CaptchaService;
import io.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import io.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaConfiguration;
import io.nixer.nixerplugin.captcha.security.BadCaptchaException;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import io.nixer.nixerplugin.core.NixerAutoConfiguration;
import io.nixer.nixerplugin.core.login.LoginFailureType;
import io.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import io.nixer.nixerplugin.captcha.CaptchaBehavior;
import io.nixer.nixerplugin.captcha.CaptchaService;
import io.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import io.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaConfiguration;
import io.nixer.nixerplugin.captcha.security.BadCaptchaException;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import io.nixer.nixerplugin.core.NixerAutoConfiguration;
import io.nixer.nixerplugin.core.login.LoginFailureType;
import io.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter.LOGIN_ACTION;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Captcha.
 *
 */
@Configuration
@EnableConfigurationProperties(value = {LoginCaptchaProperties.class})
@Import({RecaptchaConfiguration.class})
@AutoConfigureOrder(NixerAutoConfiguration.ORDER + 1)
public class CaptchaConfiguration implements LoginFailureTypeRegistry.Contributor {

    @Bean
    public CaptchaEndpoint captchaEndpoint(CaptchaChecker captchaChecker) {
        return new CaptchaEndpoint(captchaChecker);
    }

    @Bean
    public CaptchaChecker captchaChecker(CaptchaServiceFactory captchaServiceFactory,
                                         LoginCaptchaProperties loginCaptchaProperties) {
        final CaptchaService captchaService = captchaServiceFactory.createCaptchaService(CaptchaMetricsReporter.LOGIN_ACTION);
        final String captchaParam = loginCaptchaProperties.getParam();

        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, captchaParam);
        if (loginCaptchaProperties.getCondition() != null) {
            captchaChecker.setCaptchaCondition(loginCaptchaProperties.getCondition());
        }
        return captchaChecker;
    }

    @Bean
    public CaptchaValidator captchaValidator(CaptchaServiceFactory captchaServiceFactory) {
        return new CaptchaValidator(captchaServiceFactory);
    }

    @Bean
    public CaptchaBehavior captchaBehavior() {
        return new CaptchaBehavior();
    }

    @Override
    public void contribute(final LoginFailureTypeRegistry.Builder registryBuilder) {
        // register failure type for exception
        registryBuilder.addMapping(BadCaptchaException.class, LoginFailureType.INVALID_CAPTCHA);
    }

}

package eu.xword.nixer.nixerplugin.captcha.config;

import eu.xword.nixer.nixerplugin.captcha.CaptchaBehavior;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaConfiguration;
import eu.xword.nixer.nixerplugin.captcha.security.BadCaptchaException;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import eu.xword.nixer.nixerplugin.core.NixerAutoConfiguration;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter.LOGIN_ACTION;

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
        final CaptchaService captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);
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

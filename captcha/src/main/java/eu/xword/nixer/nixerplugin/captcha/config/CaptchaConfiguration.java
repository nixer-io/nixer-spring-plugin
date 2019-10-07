package eu.xword.nixer.nixerplugin.captcha.config;

import javax.annotation.PostConstruct;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import eu.xword.nixer.nixerplugin.captcha.security.BadCaptchaException;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginFailureTypeRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.LOGIN_ACTION;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Captcha.
 *
 */
@Configuration
@EnableConfigurationProperties(value = {LoginCaptchaProperties.class})
public class CaptchaConfiguration {

    LoginFailureTypeRegistry loginFailureTypeRegistry;

    public CaptchaConfiguration(LoginFailureTypeRegistry loginFailureTypeRegistry) {
        this.loginFailureTypeRegistry = loginFailureTypeRegistry;
    }

    @PostConstruct
    public void setup() {
        loginFailureTypeRegistry.addMapping(BadCaptchaException.class, LoginFailureType.INVALID_CAPTCHA);
    }

    @Bean
    public CaptchaEndpoint captchaEndpoint(CaptchaChecker captchaChecker) {
        return new CaptchaEndpoint(captchaChecker);
    }

    @Bean
    public CaptchaChecker captchaChecker(CaptchaServiceFactory captchaServiceFactory,
                                         LoginCaptchaProperties loginCaptchaProperties) {
        final CaptchaService captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);

        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService);
        captchaChecker.setCaptchaParam(loginCaptchaProperties.getParam());
        captchaChecker.setCaptchaCondition(loginCaptchaProperties.getCondition());
        return captchaChecker;
    }

    @Bean
    public CaptchaValidator captchaValidator(CaptchaServiceFactory captchaServiceFactory) {
        return new CaptchaValidator(captchaServiceFactory);
    }
}

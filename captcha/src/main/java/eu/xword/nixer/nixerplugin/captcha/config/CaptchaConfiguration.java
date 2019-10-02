package eu.xword.nixer.nixerplugin.captcha.config;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.endpoint.CaptchaEndpoint;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporterFactory;
import eu.xword.nixer.nixerplugin.captcha.metrics.MicrometerMetricsReporterFactory;
import eu.xword.nixer.nixerplugin.captcha.metrics.NOPMetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.reattempt.IdentityCreator;
import eu.xword.nixer.nixerplugin.captcha.reattempt.InMemoryCaptchaReattemptService;
import eu.xword.nixer.nixerplugin.captcha.reattempt.IpIdentityCreator;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaClient;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaConfiguration;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaV2ServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaCondition;
import eu.xword.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import eu.xword.nixer.nixerplugin.login.LoginFailures;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static eu.xword.nixer.nixerplugin.captcha.config.CaptchaProperties.MetricsProperties.DEFAULT;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.LOGIN_ACTION;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Captcha.
 *
 */
@Configuration
@EnableConfigurationProperties(value = {LoginCaptchaProperties.class})
@ConditionalOnProperty(value = "nixer.login.captcha.enabled", havingValue = "true", matchIfMissing = LoginCaptchaProperties.DEFAULT)
@Import(RecaptchaConfiguration.class)
public class CaptchaConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "captcha.metrics", name = "enabled", havingValue = "true", matchIfMissing = DEFAULT)
    public MetricsReporterFactory metricsReporterFactory(MeterRegistry meterRegistry) {
        return new MicrometerMetricsReporterFactory(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "captcha.metrics", name = "enabled", havingValue = "false")
    public MetricsReporterFactory nopMetricsReporterFactory() {
        return action -> new NOPMetricsReporter();
    }

    @Bean
    public CaptchaEndpoint captchaEndpoint(CaptchaChecker captchaChecker) {
        return new CaptchaEndpoint(captchaChecker);
    }

    @Bean
    public CaptchaChecker captchaChecker(CaptchaServiceFactory captchaServiceFactory,
                                         LoginCaptchaProperties loginCaptchaProperties,
                                         LoginFailures loginFailures) {
        final CaptchaCondition captchaCondition = CaptchaCondition.valueOf(loginCaptchaProperties.getCondition());
        final CaptchaService captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);

        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, loginFailures);
        captchaChecker.setCaptchaParam(loginCaptchaProperties.getParam());
        captchaChecker.setCaptchaCondition(captchaCondition);
        return captchaChecker;
    }

    @Bean
    public CaptchaValidator captchaValidator(CaptchaServiceFactory captchaServiceFactory) {
        return new CaptchaValidator(captchaServiceFactory);
    }

    @Bean
    //TODO conditional on property or expression
    public InMemoryCaptchaReattemptService reattemptService(CaptchaProperties captcha, IdentityCreator identityCreator) {
        final CaptchaProperties.BlockingProperties blocking = captcha.getBlocking();

        return new InMemoryCaptchaReattemptService(blocking.getMaxAttempts(), blocking.getDuration(), identityCreator);
    }

    @Bean
    public IdentityCreator subjectIpIdentityCreator() {
        return new IpIdentityCreator();
    }

    @Bean
    @ConditionalOnBean(value = {RecaptchaClient.class})
    public RecaptchaV2ServiceFactory recaptchaV2ServiceFactory(RecaptchaClient recaptchaClient, RecaptchaProperties recaptchaProperties,
                                                               MetricsReporterFactory metricsReporterFactory) {
        return new RecaptchaV2ServiceFactory(recaptchaClient, metricsReporterFactory);
    }

}

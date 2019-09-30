package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.CompositeCaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.config.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporterFactory;
import eu.xword.nixer.nixerplugin.captcha.reattempt.InMemoryCaptchaReattemptService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Returns configured instance of {@link RecaptchaV2Service}.
 */
@Component
public class RecaptchaV2ServiceFactory implements CaptchaServiceFactory {

    private RecaptchaClient recaptchaClient;
    private MetricsReporterFactory metricsReporterFactory;
    private RecaptchaProperties recaptchaProperties;
    private InMemoryCaptchaReattemptService captchaReattemptService;

    public RecaptchaV2ServiceFactory(final RecaptchaClient recaptchaClient,
                                     final MetricsReporterFactory metricsReporterFactory,
                                     final RecaptchaProperties recaptchaProperties,
                                     final InMemoryCaptchaReattemptService captchaReattemptService) {
        Assert.notNull(recaptchaClient, "RecaptchaClient must not be null");
        this.recaptchaClient = recaptchaClient;

        Assert.notNull(metricsReporterFactory, "MetricsReporterFactory must not be null");
        this.metricsReporterFactory = metricsReporterFactory;

        Assert.notNull(recaptchaProperties, "RecaptchaProperties must not be null");
        this.recaptchaProperties = recaptchaProperties;

        Assert.notNull(captchaReattemptService, "CaptchaReattemptService must not be null");
        this.captchaReattemptService = captchaReattemptService;
    }

    @Override
    public CaptchaService createCaptchaService(final String action) {
        final List<CaptchaInterceptor> interceptors = new ArrayList<>();
        final MetricsReporter metricsReporter = metricsReporterFactory.createMetricsReporter(action);
        interceptors.add(metricsReporter);

        final RecaptchaProperties.BlockingProperties blocking = recaptchaProperties.getBlocking();
        if (blocking.isEnabled()) {
            //TODO it makes blocking working on action level rather than global consider pros/cons
            interceptors.add(captchaReattemptService);
        }

        final CompositeCaptchaInterceptor captchaInterceptor = new CompositeCaptchaInterceptor(interceptors);

        return new RecaptchaV2Service(recaptchaClient, captchaInterceptor);
    }

}

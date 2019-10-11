package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporterFactory;
import org.springframework.util.Assert;

/**
 * Returns configured instance of {@link RecaptchaV2Service}.
 */
public class RecaptchaV2ServiceFactory implements CaptchaServiceFactory {

    private final RecaptchaClient recaptchaClient;
    private final MetricsReporterFactory metricsReporterFactory;

    public RecaptchaV2ServiceFactory(final RecaptchaClient recaptchaClient, final MetricsReporterFactory metricsReporterFactory) {
        Assert.notNull(recaptchaClient, "RecaptchaClient must not be null");
        this.recaptchaClient = recaptchaClient;

        Assert.notNull(metricsReporterFactory, "MetricsReporterFactory must not be null");
        this.metricsReporterFactory = metricsReporterFactory;
    }

    @Override
    public CaptchaService createCaptchaService(final String action) {
        final MetricsReporter metricsReporter = metricsReporterFactory.createMetricsReporter(action);

        return new RecaptchaV2Service(recaptchaClient, metricsReporter);
    }

}

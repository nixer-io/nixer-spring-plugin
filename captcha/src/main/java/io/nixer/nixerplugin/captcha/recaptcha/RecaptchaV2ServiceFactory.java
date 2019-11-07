package io.nixer.nixerplugin.captcha.recaptcha;

import io.nixer.nixerplugin.captcha.CaptchaService;
import io.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
import org.springframework.util.Assert;

/**
 * Returns configured instance of {@link RecaptchaV2Service}.
 */
public class RecaptchaV2ServiceFactory implements CaptchaServiceFactory {

    private final RecaptchaClient recaptchaClient;
    private final MetricsFactory metricsFactory;

    public RecaptchaV2ServiceFactory(final RecaptchaClient recaptchaClient, final MetricsFactory metricsFactory) {
        Assert.notNull(recaptchaClient, "RecaptchaClient must not be null");
        this.recaptchaClient = recaptchaClient;

        Assert.notNull(metricsFactory, "MetricsFactory must not be null");
        this.metricsFactory = metricsFactory;
    }

    @Override
    public CaptchaService createCaptchaService(final String action) {
        final CaptchaMetricsReporter metricsReporter = CaptchaMetricsReporter.create(metricsFactory, action);

        return new RecaptchaV2Service(recaptchaClient, metricsReporter);
    }

}

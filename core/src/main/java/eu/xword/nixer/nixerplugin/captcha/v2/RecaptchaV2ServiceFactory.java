package eu.xword.nixer.nixerplugin.captcha.v2;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RecaptchaV2ServiceFactory implements CaptchaServiceFactory {

    private RestTemplate restTemplate;
    private MetricsReporterFactory metricsReporterFactory;
    private RecaptchaProperties recaptchaProperties;

    public RecaptchaV2ServiceFactory(final RestTemplate restTemplate, final MetricsReporterFactory metricsReporterFactory, final RecaptchaProperties recaptchaProperties) {
        this.restTemplate = restTemplate;
        this.metricsReporterFactory = metricsReporterFactory;
        this.recaptchaProperties = recaptchaProperties;
    }

    @Override
    public CaptchaService createCaptchaService(final String action) {
        final MetricsReporter metricsReporter = metricsReporterFactory.createMetricsReporter(action);

        return new RecaptchaV2Service(restTemplate, metricsReporter, recaptchaProperties);
    }

}

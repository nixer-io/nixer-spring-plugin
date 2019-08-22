package eu.xword.nixer.nixerplugin.captcha.v2;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.CompositeCaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.reattempt.InMemoryCaptchaReattemptService;
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
    private InMemoryCaptchaReattemptService captchaReattemptService;

    public RecaptchaV2ServiceFactory(final RestTemplate restTemplate, final MetricsReporterFactory metricsReporterFactory, final RecaptchaProperties recaptchaProperties,
                                     final InMemoryCaptchaReattemptService captchaReattemptService) {
        this.restTemplate = restTemplate;
        this.metricsReporterFactory = metricsReporterFactory;
        this.recaptchaProperties = recaptchaProperties;
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

        return new RecaptchaV2Service(restTemplate, captchaInterceptor, recaptchaProperties);
    }

}

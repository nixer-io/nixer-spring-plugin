package eu.xword.nixer.nixerplugin.captcha.v2;

import eu.xword.nixer.nixerplugin.captcha.CaptchaMetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RecaptchaV2ServiceFactory implements CaptchaServiceFactory {

    private RestTemplate restTemplate;
    private MeterRegistry meterRegistry;
    private RecaptchaProperties recaptchaProperties;

    public RecaptchaV2ServiceFactory(final RestTemplate restTemplate, final MeterRegistry meterRegistry, final RecaptchaProperties recaptchaProperties) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.recaptchaProperties = recaptchaProperties;
    }

    @Override
    public CaptchaService createCaptchaService(String action) {
        final CaptchaMetricsReporter metricsReporter = new CaptchaMetricsReporter(meterRegistry, action);

        return new RecaptchaV2Service(restTemplate, metricsReporter, recaptchaProperties);
    }

}

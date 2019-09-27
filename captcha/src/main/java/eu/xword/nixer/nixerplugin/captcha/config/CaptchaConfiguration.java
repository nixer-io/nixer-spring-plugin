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
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaRestClient;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaCondition;
import eu.xword.nixer.nixerplugin.captcha.validation.CaptchaValidator;
import eu.xword.nixer.nixerplugin.login.LoginFailures;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static eu.xword.nixer.nixerplugin.captcha.config.RecaptchaProperties.MetricsProperties.DEFAULT;

@Configuration
@EnableConfigurationProperties(value = {LoginCaptchaProperties.class})
@ConditionalOnProperty(value = "nixer.login.captcha.enabled", havingValue = "true", matchIfMissing = LoginCaptchaProperties.DEFAULT)
public class CaptchaConfiguration {

    private static final String LOGIN_ACTION = "login";

    @Bean
    @ConditionalOnClass(HttpClient.class)
    public ClientHttpRequestFactory apacheClientHttpRequestFactory(RecaptchaProperties recaptcha) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(recaptcha.getHttp().getMaxConnections());

        final RecaptchaProperties.Http http = recaptcha.getHttp();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(http.getTimeout().getConnectionRequest())
                .setConnectTimeout(http.getTimeout().getConnect())
                .setSocketTimeout(http.getTimeout().getRead())
                .build();

        final CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }


    @Bean("captchaRestTemplate")
    @ConditionalOnMissingBean(name = "captchaRestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
        return new RestTemplate(requestFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "recaptcha.metrics", name = "enabled", havingValue = "true", matchIfMissing = DEFAULT)
    public MetricsReporterFactory metricsReporterFactory(MeterRegistry meterRegistry) {
        return new MicrometerMetricsReporterFactory(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
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
    public InMemoryCaptchaReattemptService reattemptService(RecaptchaProperties recaptcha, IdentityCreator identityCreator) {
        final RecaptchaProperties.BlockingProperties blocking = recaptcha.getBlocking();

        return new InMemoryCaptchaReattemptService(blocking.getMaxAttempts(), blocking.getDuration(), identityCreator);
    }

    @Bean
    public IdentityCreator subjectIpIdentityCreator() {
        return new IpIdentityCreator();
    }

    @Bean
    public RecaptchaClient captchaClient(RestTemplate restTemplate, RecaptchaProperties recaptchaProperties) {
        return new RecaptchaRestClient(restTemplate, recaptchaProperties);
    }
}

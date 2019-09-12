package eu.xword.nixer.nixerplugin.captcha.config;

import eu.xword.nixer.nixerplugin.NixerProperties;
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
import eu.xword.nixer.nixerplugin.captcha.strategy.AutomaticCaptchaStrategy;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import eu.xword.nixer.nixerplugin.captcha.strategy.StrategyRegistry;
import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "nixer.login.captcha", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CaptchaConfiguration {

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
    @ConditionalOnProperty(value = "recaptcha.enable-metrics", havingValue = "true", matchIfMissing = true)
    public MetricsReporterFactory metricsReporterFactory(MeterRegistry meterRegistry) {
        return new MicrometerMetricsReporterFactory(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricsReporterFactory nopMetricsReporterFactory() {
        return action -> new NOPMetricsReporter();
    }

    @Bean
//    @ConditionalOnEnabledEndpoint(endpoint = CaptchaEndpoint.class)
// TODO consider if needed. Check if endpoint registered if not explicitly enabled
//    @ConditionalOnBean(CaptchaChecker.class)
    @ConditionalOnMissingBean
    public CaptchaEndpoint captchaEndpoint(CaptchaChecker captchaChecker, StrategyRegistry strategyRegistry) {
        return new CaptchaEndpoint(captchaChecker, strategyRegistry);
    }

    @Bean
    public StrategyRegistry strategyRegistry(AutomaticCaptchaStrategy automaticCaptchaStrategy) {
        final StrategyRegistry strategyRegistry = new StrategyRegistry();
        strategyRegistry.registerStrategy(automaticCaptchaStrategy);
        return strategyRegistry;
    }

    @Bean
    public CaptchaChecker captchaChecker(CaptchaServiceFactory captchaServiceFactory, NixerProperties properties, StrategyRegistry strategyRegistry) {
        final CaptchaLoginProperties captchaLoginProperties = properties.getCaptcha();
        final CaptchaStrategy captchaStrategy = strategyRegistry.valueOf(captchaLoginProperties.getStrategy());
        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaServiceFactory, captchaLoginProperties);
        captchaChecker.setCaptchaStrategy(captchaStrategy);

        return captchaChecker;
    }

    @Bean
    public AutomaticCaptchaStrategy automaticCaptchaStrategy(GlobalCredentialStuffing globalCredentialStuffing) {
        return new AutomaticCaptchaStrategy(globalCredentialStuffing);
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

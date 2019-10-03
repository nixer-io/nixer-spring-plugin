package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporterFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for ReCaptcha.
 *
 */
@Configuration
public class RecaptchaConfiguration {

    @Bean
    @ConditionalOnClass(HttpClient.class)
    public ClientHttpRequestFactory recaptchaApacheClientHttpRequestFactory(RecaptchaProperties recaptcha) {
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


    @Bean("recaptchaRestTemplate")
    @ConditionalOnMissingBean(name = "recaptchaRestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
        return new RestTemplate(requestFactory);
    }

    @Bean
    public RecaptchaClient captchaClient(RestTemplate restTemplate, RecaptchaProperties recaptchaProperties) {
        return new RecaptchaRestClient(restTemplate, recaptchaProperties);
    }

    @Bean
    public RecaptchaV2ServiceFactory recaptchaV2ServiceFactory(RecaptchaClient recaptchaClient, RecaptchaProperties recaptchaProperties,
                                                               MetricsReporterFactory metricsReporterFactory) {
        return new RecaptchaV2ServiceFactory(recaptchaClient, metricsReporterFactory);
    }

}

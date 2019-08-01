package eu.xword.nixer.nixerplugin.captcha;

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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CaptchaConfiguration {

    // TODO get rid of autowired

    @Bean
    @ConditionalOnProperty(value = "recaptcha.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public CaptchaService recaptchaV2Service(CaptchaServiceFactory captchaServiceFactory) {
        return captchaServiceFactory.createCaptchaService("login");
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
        result.setMaxTotal(20); // TODO make configurable
        return result;
    }

    @Bean
    @ConditionalOnClass(org.apache.http.client.HttpClient.class)
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RecaptchaProperties recaptcha) {
        final RecaptchaProperties.Timeout timeout = recaptcha.getTimeout();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout.getConnectionRequest())
                .setConnectTimeout(timeout.getConnect())
                .setSocketTimeout(timeout.getRead())
                .build();

        return HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }


    @Bean("captchaRestTemplate")
    @ConditionalOnMissingBean(name = "captchaRestTemplate")
    @ConditionalOnClass(HttpClient.class)
    public RestTemplate restTemplate(HttpClient httpClient) {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}

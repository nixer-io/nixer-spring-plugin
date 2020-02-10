package io.nixer.nixerplugin.captcha.recaptcha;

import java.net.URI;
import java.net.URISyntaxException;

import io.nixer.nixerplugin.captcha.error.CaptchaErrors;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

/**
 * Client API for Google's Recaptcha verification endpoint
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">Developer Guid</a>
 */
public class RecaptchaRestClient implements RecaptchaClient {

    private final RestOperations restTemplate;
    private final String verifyUrl;
    private final String recaptchaSecret;

    public RecaptchaRestClient(final RestOperations restTemplate, final RecaptchaProperties config) {
        Assert.notNull(restTemplate, "RestTemplate must not be null");
        this.restTemplate = restTemplate;

        Assert.notNull(config, "RecaptchaProperties must not be null");
        this.verifyUrl = config.getVerifyUrl();
        this.recaptchaSecret = config.getKey().getSecret();
    }

    public RecaptchaVerifyResponse call(final String captcha) {
        try {
            final URI url = captchaUri(captcha);
            return restTemplate.getForObject(url, RecaptchaVerifyResponse.class);
        } catch (Exception e) {
            throw CaptchaErrors.serviceFailure("Failed calling captcha verify", e);
        }
    }

    private URI captchaUri(final String captcha) throws URISyntaxException {
        return new URIBuilder(verifyUrl)
                .addParameter("secret", recaptchaSecret)
                .addParameter("response", captcha)
                .build();
    }
}

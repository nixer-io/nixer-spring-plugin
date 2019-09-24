package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.HashMap;
import java.util.Map;

import eu.xword.nixer.nixerplugin.captcha.config.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import org.springframework.web.client.RestClientException;
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
        this.restTemplate = restTemplate;
        this.verifyUrl = config.getVerifyUrl();
        this.recaptchaSecret = config.getKey().getSecret();
    }

    public RecaptchaVerifyResponse call(final String captcha) {
        final String url = verifyUrl + "?secret={secret}&response={response}";

        final Map<String, String> params = new HashMap<>();
        params.put("secret", recaptchaSecret);
        params.put("response", captcha);
        // TODO report service metrics (timeout/response time)
        try {
            return restTemplate.getForObject(url, RecaptchaVerifyResponse.class, params);
        } catch (RestClientException e) {
            throw CaptchaErrors.serviceFailure("Failed calling eu.xword.nixer.nixerplugin.captcha verify", e);
        }
    }
}

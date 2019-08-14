package eu.xword.nixer.nixerplugin.captcha.v2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import eu.xword.nixer.nixerplugin.captcha.metrics.MetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaVerifyResponse;
import eu.xword.nixer.nixerplugin.captcha.error.FallbackMode;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class RecaptchaV2Service implements CaptchaService {

    private final Log logger = LogFactory.getLog(getClass());

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private RestOperations restTemplate;
    private MetricsReporter metricsReporter;

    private String verifyUrl;
    private String recaptchaSecret;
    private FallbackMode fallbackMode;

    public RecaptchaV2Service(final RestOperations restTemplate, final MetricsReporter metricsReporter, final RecaptchaProperties config) {
        this.restTemplate = restTemplate;
        this.metricsReporter = metricsReporter;
        this.fallbackMode = config.getFallback();
        this.recaptchaSecret = config.getKey().getSecret();
        this.verifyUrl = config.getVerifyUrl();
    }

    private boolean isInValidFormat(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public void processResponse(final String captcha) {
        if (!isInValidFormat(captcha)) {
            metricsReporter.reportFailedCaptcha(); // TODO rethink
            throw CaptchaErrors.invalidCaptchaFormat("Response contains invalid characters");
        }

        try {
            verify(captcha);

            metricsReporter.reportPassedCaptcha();
        } catch (RecaptchaServiceException e) {
            metricsReporter.reportFailedCaptcha(); // TODO rethink
            fallbackMode.handle(e);
        } catch (RecaptchaClientException e) {
            // TODO think of retry fallback mode (which exception are retryable?) TIMEOUT
            metricsReporter.reportFailedCaptcha();
            throw e;
        }
    }

    private void verify(final String captcha) {
        final CaptchaVerifyResponse verifyResponse = call(captcha);

        if (!verifyResponse.isSuccess()) {
            if (!verifyResponse.hasClientError()) {
                logger.warn("Got captcha verify error: " + Arrays.toString(verifyResponse.getErrorCodes()));
            }
            throw CaptchaErrors.invalidRecaptcha("reCaptcha was not successfully validated");
        }
    }

    private CaptchaVerifyResponse call(final String captcha) {
        String url = verifyUrl + "?secret={secret}&response={response}";

        final Map<String, String> params = new HashMap<>();
        params.put("secret", recaptchaSecret);
        params.put("response", captcha);
        // TODO report service metrics (timeout/response time)
        // TODO extract captcha client
        // TODO remember captcha results per user basis (token, ip) - requires cache/db
        try {
            return restTemplate.getForObject(url, CaptchaVerifyResponse.class, params);
        } catch (RestClientException e) {
            throw CaptchaErrors.serviceFailure("Failed calling captcha verify", e);
        }
    }
}

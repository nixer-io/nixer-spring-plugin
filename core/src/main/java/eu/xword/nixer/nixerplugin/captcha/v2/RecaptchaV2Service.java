package eu.xword.nixer.nixerplugin.captcha.v2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaVerifyResponse;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import eu.xword.nixer.nixerplugin.captcha.error.FallbackMode;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

/**
 * Implementation of {@link CaptchaService} for Google's ReCaptcha V2.
 * <p>
 * Uses Google REST API to verify captcha
 */
public class RecaptchaV2Service implements CaptchaService {

    private final Log logger = LogFactory.getLog(getClass());

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private RestOperations restTemplate;
    private CaptchaInterceptor captchaInterceptor;

    private String verifyUrl;
    private String recaptchaSecret;
    private FallbackMode fallbackMode;

    public RecaptchaV2Service(final RestOperations restTemplate, final CaptchaInterceptor captchaInterceptor, final RecaptchaProperties config) {
        this.restTemplate = restTemplate;
        this.captchaInterceptor = captchaInterceptor;
        this.fallbackMode = config.getFallback();
        this.recaptchaSecret = config.getKey().getSecret();
        this.verifyUrl = config.getVerifyUrl();
    }

    private boolean isInValidFormat(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public void processResponse(final String captcha) {
        captchaInterceptor.onCheck();

        if (!isInValidFormat(captcha)) {
            captchaInterceptor.onFailure(); // TODO rethink
            throw CaptchaErrors.invalidCaptchaFormat("Response contains invalid characters");
        }

        try {
            verify(captcha);

            captchaInterceptor.onSuccess();
        } catch (RecaptchaServiceException e) {
            captchaInterceptor.onFailure(); // TODO rethink
            fallbackMode.handle(e);
        } catch (RecaptchaClientException e) {
            // TODO think of retry fallback mode (which exception are retryable?) TIMEOUT
            captchaInterceptor.onFailure();
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

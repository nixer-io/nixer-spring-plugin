package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.regex.Pattern;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link CaptchaService} for Google's ReCaptcha V2.
 * <p>
 * Uses Google REST API to verify captcha
 */
public class RecaptchaV2Service implements CaptchaService {

    private final Log logger = LogFactory.getLog(getClass());

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private final CaptchaInterceptor captchaInterceptor;

    private final RecaptchaClient recaptchaClient;

    public RecaptchaV2Service(final RecaptchaClient recaptchaClient, final CaptchaInterceptor captchaInterceptor) {
        Assert.notNull(recaptchaClient, "RecaptchaClient must not be null");
        this.recaptchaClient = recaptchaClient;

        Assert.notNull(captchaInterceptor, "CaptchaInterceptor must not be null");
        this.captchaInterceptor = captchaInterceptor;
    }

    private boolean isInValidFormat(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public void verifyResponse(final String captcha) {
        captchaInterceptor.onCheck();

        if (!isInValidFormat(captcha)) {
            captchaInterceptor.onFailure();
            throw CaptchaErrors.invalidCaptchaFormat("Response contains invalid characters");
        }

        try {
            verify(captcha);

            captchaInterceptor.onSuccess();
        } catch (CaptchaServiceException | CaptchaClientException e) {
            captchaInterceptor.onFailure();
            throw e;
        }
    }

    private void verify(final String captcha) {
        final RecaptchaVerifyResponse verifyResponse = recaptchaClient.call(captcha);

        if (!verifyResponse.isSuccess()) {
            if (!verifyResponse.hasClientError()) {
                logger.warn("Got captcha verify error: " + verifyResponse.getErrorCodes());
            }
            throw CaptchaErrors.invalidCaptcha("reCaptcha was not successfully validated");
        }
    }

}

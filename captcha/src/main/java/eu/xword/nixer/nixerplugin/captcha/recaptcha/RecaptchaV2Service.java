package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.regex.Pattern;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
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

    private final CaptchaMetricsReporter metricsReporter;

    private final RecaptchaClient recaptchaClient;

    public RecaptchaV2Service(final RecaptchaClient recaptchaClient, final CaptchaMetricsReporter metricsReporter) {
        Assert.notNull(recaptchaClient, "RecaptchaClient must not be null");
        this.recaptchaClient = recaptchaClient;

        Assert.notNull(metricsReporter, "CaptchaMetricsReporter must not be null");
        this.metricsReporter = metricsReporter;
    }

    private boolean isInValidFormat(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public void verifyResponse(final String captcha) {
        if (!isInValidFormat(captcha)) {
            metricsReporter.reportFailedCaptcha();
            throw CaptchaErrors.invalidCaptchaFormat("Response contains invalid characters");
        }

        try {
            verify(captcha);

            metricsReporter.reportPassedCaptcha();
        } catch (CaptchaServiceException | CaptchaClientException e) {
            metricsReporter.reportFailedCaptcha();
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

package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import com.google.common.collect.ImmutableList;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaServiceException;
import eu.xword.nixer.nixerplugin.captcha.metrics.MicrometerMetricsReporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaVerifyResponse.ErrorCode.InvalidResponse;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecaptchaV2ServiceTest {

    RecaptchaV2Service captchaService;

    @Mock
    RecaptchaClient recaptchaClient;

    @Mock
    MicrometerMetricsReporter captchaMetricsReporter;

    @BeforeEach
    public void init() {
        captchaService = new RecaptchaV2Service(recaptchaClient, captchaMetricsReporter);
    }

    @Test
    public void should_throw_exception_when_captcha_empty() {
        Assertions.assertThrows(RecaptchaClientException.class, () -> captchaService.processResponse(""));
    }

    @Test
    public void should_throw_exception_when_captcha_misformatted() {
        Assertions.assertThrows(RecaptchaClientException.class, () -> captchaService.processResponse("!"));
    }

    @Test
    public void should_throw_exception_when_got_timeout() {
        given(recaptchaClient.call("good"))
                .willThrow(CaptchaErrors.serviceFailure("timeout", new RestClientException("timeout")));

        Assertions.assertThrows(RecaptchaServiceException.class, () -> captchaService.processResponse("good"));
    }

    @Test
    public void should_throw_exception_when_error_received() {
        given(recaptchaClient.call("bad"))
                .willReturn(new RecaptchaVerifyResponse(false, "", "host", ImmutableList.of(InvalidResponse)));

        Assertions.assertThrows(RecaptchaClientException.class, () -> captchaService.processResponse("bad"));
    }

    @Test
    public void should_not_throw_exception_if_ok() {
        given(recaptchaClient.call("good"))
                .willReturn(new RecaptchaVerifyResponse(true, "", "host", ImmutableList.of()));

        captchaService.processResponse("good");
    }

}

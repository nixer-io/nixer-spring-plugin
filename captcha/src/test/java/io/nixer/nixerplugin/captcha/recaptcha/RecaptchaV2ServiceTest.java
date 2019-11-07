package io.nixer.nixerplugin.captcha.recaptcha;

import com.google.common.collect.ImmutableList;
import io.nixer.nixerplugin.captcha.error.CaptchaErrors;
import io.nixer.nixerplugin.captcha.error.CaptchaClientException;
import io.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
import io.nixer.nixerplugin.captcha.metrics.CaptchaMetricsReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static io.nixer.nixerplugin.captcha.recaptcha.RecaptchaVerifyResponse.ErrorCode.InvalidResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecaptchaV2ServiceTest {

    private RecaptchaV2Service captchaService;

    @Mock
    private RecaptchaClient recaptchaClient;

    @Mock
    private CaptchaMetricsReporter captchaMetricsReporter;

    @BeforeEach
    void init() {
        captchaService = new RecaptchaV2Service(recaptchaClient, captchaMetricsReporter);
    }

    @Test
    void should_throw_exception_when_captcha_empty() {
        assertThrows(CaptchaClientException.class, () -> captchaService.verifyResponse(""));
    }

    @Test
    void should_throw_exception_when_captcha_misformatted() {
        assertThrows(CaptchaClientException.class, () -> captchaService.verifyResponse("!"));
    }

    @Test
    void should_throw_exception_when_got_timeout() {
        given(recaptchaClient.call("good"))
                .willThrow(CaptchaErrors.serviceFailure("timeout", new RestClientException("timeout")));

        assertThrows(CaptchaServiceException.class, () -> captchaService.verifyResponse("good"));
    }

    @Test
    void should_throw_exception_when_error_received() {
        given(recaptchaClient.call("bad"))
                .willReturn(new RecaptchaVerifyResponse(false, "", "host", ImmutableList.of(RecaptchaVerifyResponse.ErrorCode.InvalidResponse)));

        assertThrows(CaptchaClientException.class, () -> captchaService.verifyResponse("bad"));
    }

    @Test
    void should_not_throw_exception_if_ok() {
        given(recaptchaClient.call("good"))
                .willReturn(new RecaptchaVerifyResponse(true, "", "host", ImmutableList.of()));

        captchaService.verifyResponse("good");
    }

}

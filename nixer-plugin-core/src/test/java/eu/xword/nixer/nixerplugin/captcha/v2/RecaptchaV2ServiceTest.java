package eu.xword.nixer.nixerplugin.captcha.v2;

import eu.xword.nixer.nixerplugin.captcha.metrics.MicrometerMetricsReporter;
import eu.xword.nixer.nixerplugin.captcha.CaptchaVerifyResponse;
import eu.xword.nixer.nixerplugin.captcha.CaptchaVerifyResponse.ErrorCode;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaClientException;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import static eu.xword.nixer.nixerplugin.captcha.CaptchaVerifyResponse.ErrorCode.InvalidResponse;

@ExtendWith(MockitoExtension.class)
class RecaptchaV2ServiceTest {

    RecaptchaV2Service captchaService;

    @Mock
    RestOperations restOperations;

    @Mock
    MicrometerMetricsReporter captchaMetricsReporter;

    @BeforeEach
    public void init() {
        final RecaptchaProperties props = new RecaptchaProperties();

        captchaService = new RecaptchaV2Service(restOperations, captchaMetricsReporter, props);
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
        whenRestCalled(restOperations)
                .thenThrow(new RestClientException("timeout"));

        Assertions.assertThrows(RecaptchaServiceException.class, () -> captchaService.processResponse("good"));
    }

    @Test
    public void should_throw_exception_when_error_received() {
        whenRestCalled(restOperations)
                .thenReturn(new CaptchaVerifyResponse(false, "", "host", new ErrorCode[]{InvalidResponse}));

        Assertions.assertThrows(RecaptchaClientException.class, () -> captchaService.processResponse("bad"));
    }

    @Test
    public void should_not_throw_exception_if_ok() {
        whenRestCalled(restOperations)
                .thenReturn(new CaptchaVerifyResponse(true, "", "host", new ErrorCode[]{}));

        captchaService.processResponse("good");
    }

    private OngoingStubbing<CaptchaVerifyResponse> whenRestCalled(final RestOperations restOperations) {
        return Mockito.when(restOperations.getForObject(Mockito.anyString(), Mockito.eq(CaptchaVerifyResponse.class), Mockito.anyMap()));
    }
}

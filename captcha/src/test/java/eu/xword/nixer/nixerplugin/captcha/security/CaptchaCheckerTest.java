package eu.xword.nixer.nixerplugin.captcha.security;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;

class CaptchaCheckerTest {

    private static final UserDetails ANY = null;
    private static final String CAPTCHA_PARAM = "captcha-param";

    private CaptchaService captchaService = BDDMockito.mock(CaptchaService.class);

    private CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, CAPTCHA_PARAM);

    @Test
    void should_pass_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doNothing().when(captchaService).verifyResponse("captcha");

        captchaChecker.check(ANY);
    }

    @Test
    void should_fail_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doThrow(new CaptchaServiceException(""))
                .when(captchaService).verifyResponse("captcha");

        assertThrows(BadCaptchaException.class, () -> captchaChecker.check(ANY));
    }

    @Test
    void should_skip_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        captchaChecker.setCaptchaCondition(CaptchaCondition.NEVER);
        captchaChecker.setRequest(request);

        captchaChecker.check(ANY);

        verifyZeroInteractions(captchaService);
    }
}
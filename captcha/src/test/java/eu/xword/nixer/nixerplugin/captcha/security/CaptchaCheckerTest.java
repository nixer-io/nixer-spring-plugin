package eu.xword.nixer.nixerplugin.captcha.security;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaServiceException;
import eu.xword.nixer.nixerplugin.login.LoginFailures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import static eu.xword.nixer.nixerplugin.captcha.config.RecaptchaProperties.DEFAULT_CAPTCHA_PARAM;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class CaptchaCheckerTest {

    public static final UserDetails ANY = null;

    @Mock
    CaptchaService captchaService;

    @Mock
    LoginFailures loginFailures;

    @Test
    public void should_pass_validation() {
        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, loginFailures);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(DEFAULT_CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doNothing().when(captchaService).processResponse("captcha");

        captchaChecker.check(ANY);
    }

    @Test
    public void should_fail_validation() {
        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, loginFailures);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(DEFAULT_CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doThrow(new RecaptchaServiceException(""))
                .when(captchaService).processResponse("captcha");

        assertThrows(BadCaptchaException.class, () -> captchaChecker.check(ANY));
    }

    @Test
    public void should_skip_validation() {
        final CaptchaChecker captchaChecker = new CaptchaChecker(captchaService, loginFailures);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        captchaChecker.setCaptchaCondition(CaptchaCondition.NEVER);
        captchaChecker.setRequest(request);

        captchaChecker.check(ANY);

        verifyZeroInteractions(captchaService);
    }
}
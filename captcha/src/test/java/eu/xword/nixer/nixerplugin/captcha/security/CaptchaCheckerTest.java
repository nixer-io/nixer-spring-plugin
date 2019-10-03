package eu.xword.nixer.nixerplugin.captcha.security;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import eu.xword.nixer.nixerplugin.login.LoginFailureTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class CaptchaCheckerTest {

    public static final UserDetails ANY = null;
    public static final String CAPTCHA_PARAM = "captcha-param";

    @Mock
    CaptchaService captchaService;

    @Mock
    LoginFailureTypeRegistry loginFailureTypeRegistry;

    CaptchaChecker captchaChecker;

    @BeforeEach
    public void setup() {
        captchaChecker = new CaptchaChecker(captchaService, loginFailureTypeRegistry);
        captchaChecker.setCaptchaParam(CAPTCHA_PARAM);
    }

    @Test
    public void should_pass_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doNothing().when(captchaService).verifyResponse("captcha");

        captchaChecker.check(ANY);
    }

    @Test
    public void should_fail_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CAPTCHA_PARAM, "captcha");
        captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);
        captchaChecker.setRequest(request);

        doThrow(new CaptchaServiceException(""))
                .when(captchaService).verifyResponse("captcha");

        assertThrows(BadCaptchaException.class, () -> captchaChecker.check(ANY));
    }

    @Test
    public void should_skip_validation() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        captchaChecker.setCaptchaCondition(CaptchaCondition.NEVER);
        captchaChecker.setRequest(request);

        captchaChecker.check(ANY);

        verifyZeroInteractions(captchaService);
    }
}
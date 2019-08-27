package eu.xword.nixer.nixerplugin.captcha.strategy;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AutomaticCaptchaStrategyTest {

    @Mock
    private GlobalCredentialStuffing globalCredentialStuffing;

    private AutomaticCaptchaStrategy captchaStrategy;

    private MockHttpServletRequest request;

    public static class MockTimedHttpSession extends org.springframework.mock.web.MockHttpSession {

        private long creationTime = System.currentTimeMillis();

        @Override
        public long getCreationTime() {
            return this.creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }
    }

    @BeforeEach
    public void setup() {
        captchaStrategy = new AutomaticCaptchaStrategy(globalCredentialStuffing);

        this.request = new MockHttpServletRequest("POST", "/login");
        MockTimedHttpSession session = new MockTimedHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void shouldChallenge() {
        given(globalCredentialStuffing.isCredentialStuffingActive()).willReturn(Boolean.TRUE);

        assertTrue(captchaStrategy.challenge());
    }

    @Test
    public void shouldNotChallenge() {
        given(globalCredentialStuffing.isCredentialStuffingActive()).willReturn(Boolean.FALSE);

        assertFalse(captchaStrategy.challenge());
    }

    @Test
    public void shouldVerifyChallengeIfSessionNotAvailableButCSActive() {
        given(globalCredentialStuffing.isCredentialStuffingActive()).willReturn(Boolean.TRUE);
        request.setSession(null);

        assertTrue(captchaStrategy.verifyChallenge());
    }

    @Test
    public void shouldNotVerifyChallengeIfSessionNotAvailableButCSInactive() {
        given(globalCredentialStuffing.isCredentialStuffingActive()).willReturn(Boolean.FALSE);
        request.setSession(null);

        assertFalse(captchaStrategy.verifyChallenge());
    }

    @Test
    public void shouldVerifyChallenge() {
        given(globalCredentialStuffing.isCredentialStuffingActive()).willReturn(Boolean.TRUE);
        request.setSession(null);

        assertTrue(captchaStrategy.verifyChallenge());
    }
}
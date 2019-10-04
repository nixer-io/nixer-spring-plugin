package eu.xword.nixer.nixerplugin.captcha;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static eu.xword.nixer.nixerplugin.captcha.CaptchaBehavior.CAPTCHA_CHALLENGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CaptchaBehaviorTest {

    CaptchaBehavior captchaBehavior;

    @BeforeEach
    public void setup() {
        captchaBehavior = new CaptchaBehavior();
    }

    /**
     * Captcha is verified on POST /login when session was not created
     */
    @Test
    @Disabled
    public void should_set_flag_to_display_and_verify_captcha_if_session_is_missing() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(null);

        act(request);

        assertEquals(true, request.getAttribute(CAPTCHA_CHALLENGE));
    }

    /**
     * For GET /login captcha is returned
     */
    @Test
    public void should_set_flag_to_display_captcha_if_session_present() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        act(request);

        assertEquals(true, session.getAttribute(CAPTCHA_CHALLENGE));
    }

    private void act(HttpServletRequest request) throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        captchaBehavior.act(request, response);
    }
}
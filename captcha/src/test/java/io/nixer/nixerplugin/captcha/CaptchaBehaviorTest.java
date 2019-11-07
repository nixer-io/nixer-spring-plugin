package io.nixer.nixerplugin.captcha;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static io.nixer.nixerplugin.captcha.CaptchaBehavior.CAPTCHA_CHALLENGE_SESSION_ATTR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CaptchaBehaviorTest {

    private CaptchaBehavior captchaBehavior = new CaptchaBehavior();

    @Test
    void should_set_flag_to_display_captcha_if_session_present() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        act(request);

        assertEquals(true, session.getAttribute(CAPTCHA_CHALLENGE_SESSION_ATTR));
    }

    private void act(HttpServletRequest request) throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        captchaBehavior.act(request, response);
    }
}

package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedirectBehaviorTest {


    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void shouldRedirect() throws IOException {
        RedirectBehavior behavior = new RedirectBehavior("/error", "redirectToError");

        behavior.act(request, response);

        assertEquals(SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/error", response.getRedirectedUrl());
        assertTrue(response.isCommitted());
    }
}
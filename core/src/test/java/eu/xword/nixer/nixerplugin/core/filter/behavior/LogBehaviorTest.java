package eu.xword.nixer.nixerplugin.core.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.xword.nixer.nixerplugin.core.ip.IpMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.IP_METADATA;
import static org.assertj.core.api.Assertions.assertThat;

class LogBehaviorTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
    private final LogBehaviorWrapper behavior = new LogBehaviorWrapper();

    @Test
    void shouldIncludeQueryString() {
        request.setQueryString("something");
        behavior.setIncludeQueryString(true);

        String message = act();

        assertThat(message).contains("uri=/login");
    }

    @Test
    void shouldIncludeHeaders() {
        behavior.setIncludeHeaders(true);

        request.addHeader(HttpHeaders.USER_AGENT, "user-agent");
        request.addHeader(HttpHeaders.ACCEPT, "text/html");
        request.addHeader(HttpHeaders.ACCEPT, "text/json");

        String message = act();

        assertThat(message).contains("User-Agent:\"user-agent\", Accept:\"text/html\", \"text/json\"");
    }

    @Test
    void shouldIncludeUser() {
        behavior.setIncludeUserInfo(true);

        request.setRemoteUser("username");
        request.setSession(new MockHttpSession(null, "123"));

        String message = act();

        assertThat(message).contains("session=123");
        assertThat(message).contains("client=127.0.0.1");
        assertThat(message).contains("user=username");
    }

    @Test
    void shouldIncludeMetadata() {
        behavior.setIncludeMetadata(true);

        request.setAttribute(GLOBAL_CREDENTIAL_STUFFING, true);
        request.setAttribute(IP_METADATA, new IpMetadata(true));

        String message = act();

        assertThat(message).contains("nixer.cs.global=true");
        assertThat(message).contains("nixer.ip.metadata=IpMetadata{blacklisted=true}");
    }

    @Test
    void shouldNotIncludeMetadataIfNone() {
        behavior.setIncludeMetadata(true);

        String message = act();

        assertThat(message).contains("attributes=[]");
    }

    @Test
    void shouldIncludeAll() {
        behavior.setIncludeUserInfo(true);
        behavior.setIncludeHeaders(true);
        behavior.setIncludeQueryString(true);
        behavior.setIncludeMetadata(true);

        request.addHeader(HttpHeaders.USER_AGENT, "user-agent");
        request.setAttribute(GLOBAL_CREDENTIAL_STUFFING, true);

        String message = act();

        assertThat(message).isEqualTo("RQ=uri=/login;client=127.0.0.1;headers=[User-Agent:\"user-agent\"];attributes=[nixer.cs.global=true]");
    }

    private String act() {
        behavior.act(request, null);
        return behavior.message;
    }

    public static class LogBehaviorWrapper extends LogBehavior {

        private String message;

        @Override
        public void act(final HttpServletRequest request, final HttpServletResponse response) {
            this.message = createMessage(request);
        }
    }
}
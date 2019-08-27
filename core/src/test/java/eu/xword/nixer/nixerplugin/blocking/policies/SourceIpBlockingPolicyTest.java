package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;

import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SourceIpBlockingPolicyTest {

    SourceIpBlockingPolicy blockingPolicy;
    MockMitigationStrategy mockMitigationStrategy;

    @BeforeEach
    public void setup() {
        mockMitigationStrategy = new MockMitigationStrategy();
        blockingPolicy = new SourceIpBlockingPolicy(mockMitigationStrategy);
    }

    @Test
    public void shouldBlockRequestBasedOnIpHeader() throws IOException {
        blockingPolicy.onApplicationEvent(new BlockSourceIPEvent("127.0.0.1"));

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setRemoteAddr("127.0.0.1");

        blockingPolicy.apply(request, new MockHttpServletResponse());
        assertThat(mockMitigationStrategy.getRequests()).hasSize(1);
    }

    @Test
    public void shouldBlockRequestBasedOnForwardedForHeader() throws IOException {
        //TODO
    }
}
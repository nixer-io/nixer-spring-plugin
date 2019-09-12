package eu.xword.nixer.nixerplugin.blocking.policies;

import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class SourceIpFilterTest {

    SourceIpFilter blockingPolicy;

    @BeforeEach
    public void setup() {
        blockingPolicy = new SourceIpFilter();
        blockingPolicy.setMitigationStrategy(new MockMitigationStrategy());
    }

    @Test
    public void shouldBlockRequestBasedOnIp() {
        blockingPolicy.onApplicationEvent(new BlockSourceIPEvent("127.0.0.1"));

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setRemoteAddr("127.0.0.1");

        assertThat(blockingPolicy.applies(request)).isTrue();
    }

}
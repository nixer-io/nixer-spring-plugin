package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.events.BlockSourceIpEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TemporalIpFilterTest {

    TemporalIpFilter blockingPolicy;

    @BeforeEach
    public void setup() {
        blockingPolicy = new TemporalIpFilter();
        blockingPolicy.setMitigationStrategy(new MockMitigationStrategy());
    }

    @Test
    public void shouldBlockRequestBasedOnIp() {
        blockingPolicy.onApplicationEvent(new BlockSourceIpEvent("127.0.0.1"));

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setRemoteAddr("127.0.0.1");

        assertThat(blockingPolicy.applies(request)).isTrue();
    }

}
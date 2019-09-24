package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.registry.BlockedIpRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TemporalIpFilterTest {

    TemporalIpFilter filter;

    @Mock
    BlockedIpRegistry blockedIpRegistry;

    @BeforeEach
    public void setup() {
        filter = new TemporalIpFilter(blockedIpRegistry);
    }

    @Test
    public void shouldBlockRequestBasedOnIp() {
        given(blockedIpRegistry.isBlocked("127.0.0.1")).willReturn(Boolean.TRUE);

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setRemoteAddr("127.0.0.1");

        filter.apply(request);

        assertThat(request.getAttribute(RequestAugmentation.IP_BLOCKED)).isEqualTo(Boolean.TRUE);
    }

}
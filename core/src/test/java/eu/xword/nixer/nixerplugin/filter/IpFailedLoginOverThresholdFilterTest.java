package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.registry.IpOverLoginThresholdRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IpFailedLoginOverThresholdFilterTest {

    IpFailedLoginOverThresholdFilter filter;

    @Mock
    IpOverLoginThresholdRegistry IPOverLoginThresholdRegistry;

    @BeforeEach
    public void setup() {
        filter = new IpFailedLoginOverThresholdFilter(IPOverLoginThresholdRegistry);
    }

    @Test
    public void shouldMarkRequestBasedOnIp() {
        given(IPOverLoginThresholdRegistry.contains("127.0.0.1")).willReturn(Boolean.TRUE);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        filter.apply(request);

        assertThat(request.getAttribute(RequestAugmentation.IP_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(Boolean.TRUE);
    }

}
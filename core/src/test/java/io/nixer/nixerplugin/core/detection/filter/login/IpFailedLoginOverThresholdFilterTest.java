package io.nixer.nixerplugin.core.detection.filter.login;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
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
    void setup() {
        filter = new IpFailedLoginOverThresholdFilter(IPOverLoginThresholdRegistry);
    }

    @Test
    void shouldMarkRequestBasedOnIp() {
        given(IPOverLoginThresholdRegistry.contains("127.0.0.1")).willReturn(Boolean.TRUE);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        filter.apply(request);

        assertThat(request.getAttribute(RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(Boolean.TRUE);
    }

}

package io.nixer.nixerplugin.core.detection.filter.login;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IpFailedLoginOverThresholdFilterTest {

    @InjectMocks
    IpFailedLoginOverThresholdFilter filter;

    @Mock
    IpOverLoginThresholdRegistry IPOverLoginThresholdRegistry;

    @Test
    void shouldMarkRequestBasedOnIp() {
        // given
        given(IPOverLoginThresholdRegistry.contains("1.2.3.4")).willReturn(true);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setRemoteAddr("1.2.3.4");

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(TRUE);
    }

}

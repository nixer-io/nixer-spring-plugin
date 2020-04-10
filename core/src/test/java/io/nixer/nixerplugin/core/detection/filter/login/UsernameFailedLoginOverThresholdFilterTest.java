package io.nixer.nixerplugin.core.detection.filter.login;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.UsernameOverLoginThresholdRegistry;
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
class UsernameFailedLoginOverThresholdFilterTest {

    @InjectMocks
    UsernameFailedLoginOverThresholdFilter filter;

    @Mock
    UsernameOverLoginThresholdRegistry usernameOverLoginThresholdRegistry;

    @Test
    public void shouldMarkRequestBasedOnUsername() {
        // given
        given(usernameOverLoginThresholdRegistry.contains("user")).willReturn(true);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setParameter("username", "user");

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(TRUE);
    }

}

package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.registry.UsernameOverLoginThresholdRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UsernameFailedLoginOverThresholdFilterTest {

    UsernameFailedLoginOverThresholdFilter filter;

    @Mock
    UsernameOverLoginThresholdRegistry usernameOverLoginThresholdRegistry;

    @BeforeEach
    public void setup() {
        filter = new UsernameFailedLoginOverThresholdFilter(usernameOverLoginThresholdRegistry);
    }

    @Test
    public void shouldMarkRequestBasedOnUsername() {
        given(usernameOverLoginThresholdRegistry.contains("user")).willReturn(Boolean.TRUE);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/login");
        request.setMethod("POST");
        request.setParameter("username", "user");

        filter.apply(request);

        assertThat(request.getAttribute(RequestAugmentation.USERNAME_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(Boolean.TRUE);
    }

}
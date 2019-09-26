package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.registry.BlockedUserRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UsernameLoginFilterTest {

    UsernameLoginFilter filter;

    @Mock
    BlockedUserRegistry blockedUserRegistry;

    @BeforeEach
    public void setup() {
        filter = new UsernameLoginFilter(blockedUserRegistry);
    }

    @Test
    public void shouldMarkRequestBasedOnUsername() {
        given(blockedUserRegistry.isBlocked("user")).willReturn(Boolean.TRUE);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/login");
        request.setMethod("POST");
        request.setParameter("username", "user");

        filter.apply(request);

        assertThat(request.getAttribute(RequestAugmentation.USERNAME_BLOCKED)).isEqualTo(Boolean.TRUE);
    }

}
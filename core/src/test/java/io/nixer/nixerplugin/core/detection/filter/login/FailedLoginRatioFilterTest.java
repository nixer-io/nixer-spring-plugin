package io.nixer.nixerplugin.core.detection.filter.login;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.FailedLoginRatioRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedLoginRatioFilterTest {

    private FailedLoginRatioFilter filter;

    @Mock
    private FailedLoginRatioRegistry registry;

    @BeforeEach
    void setup() {
        filter = new FailedLoginRatioFilter(registry);
    }

    @Test
    void metadataWhenActive() {
        when(registry.isFailedLoginRatioActivated()).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();

        filter.apply(request);

        assertThat(request.getAttribute(RequestMetadata.FAILED_LOGIN_RATIO_ACTIVE)).isEqualTo(true);
    }

    @Test
    void metadataWhenNotActive() {
        when(registry.isFailedLoginRatioActivated()).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        filter.apply(request);

        assertThat(request.getAttribute(RequestMetadata.FAILED_LOGIN_RATIO_ACTIVE)).isEqualTo(false);
    }
}

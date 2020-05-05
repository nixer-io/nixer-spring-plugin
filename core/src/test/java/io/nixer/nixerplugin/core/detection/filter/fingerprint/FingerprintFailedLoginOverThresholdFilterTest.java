package io.nixer.nixerplugin.core.detection.filter.fingerprint;

import javax.servlet.http.Cookie;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.FingerprintFailedLoginOverThresholdRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FingerprintFailedLoginOverThresholdFilterTest {

    private static final String FINGERPRINT_COOKIE_NAME = "fgprt";
    private static final String FINGERPRINT = "fingerprint-value";

    @Mock
    FingerprintFailedLoginOverThresholdRegistry registry;

    FingerprintFailedLoginOverThresholdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new FingerprintFailedLoginOverThresholdFilter(FINGERPRINT_COOKIE_NAME, registry);
    }

    @Test
    void should_set_request_metadata_for_fingerprint_present_and_threshold_exceeded() {
        // given
        given(registry.contains(FINGERPRINT)).willReturn(true);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(FINGERPRINT_COOKIE_NAME, FINGERPRINT));

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(true);
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_VALUE)).isEqualTo(FINGERPRINT);
    }

    @Test
    void should_set_request_metadata_for_fingerprint_present_and_threshold_not_exceeded() {
        // given
        given(registry.contains(FINGERPRINT)).willReturn(false);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(FINGERPRINT_COOKIE_NAME, FINGERPRINT));

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(false);
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_VALUE)).isEqualTo(FINGERPRINT);
    }

    @Test
    void should_set_request_metadata_for_fingerprint_absent() {
        // given
        final MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(false);
        assertThat(request.getAttribute(RequestMetadata.FINGERPRINT_VALUE)).isNull();
    }
}
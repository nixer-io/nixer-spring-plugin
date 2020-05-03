package io.nixer.nixerplugin.core.fingerprint;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created on 01/05/2020.
 *
 * @author Grzegorz Cwiak
 */
@ExtendWith(MockitoExtension.class)
class FingerprintAssuringFilterTest {

    private static final String NEW_FINGERPRINT = "new-fingerprint";
    private static final String COOKIE_NAME = "fgprt";

    @Mock
    FingerprintGenerator fingerprintGenerator;

    @Mock
    FilterChain filterChain;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    FingerprintAssuringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new FingerprintAssuringFilter(COOKIE_NAME, fingerprintGenerator);
    }

    @AfterEach
    void verifyFilterChainContinues() throws IOException, ServletException {
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void should_assign_fingerprint_when_not_present() throws ServletException, IOException {
        // given
        given(fingerprintGenerator.newFingerprint()).willReturn(NEW_FINGERPRINT);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getCookie(COOKIE_NAME))
                .isNotNull()
                .extracting(Cookie::getValue).isEqualTo(NEW_FINGERPRINT);
    }

    @Test
    void should_assign_fingerprint_when_empty() throws ServletException, IOException {
        // given
        request.setCookies(new Cookie(COOKIE_NAME, ""));
        given(fingerprintGenerator.newFingerprint()).willReturn(NEW_FINGERPRINT);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getCookie(COOKIE_NAME))
                .isNotNull()
                .extracting(Cookie::getValue).isEqualTo(NEW_FINGERPRINT);
    }

    @Test
    void should_not_assign_fingerprint_when_present() throws ServletException, IOException {
        // given
        request.setCookies(new Cookie(COOKIE_NAME, "old-fingerprint"));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getCookie(COOKIE_NAME)).isNull();
    }
}
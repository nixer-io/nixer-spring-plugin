package io.nixer.nixerplugin.stigma.detection;

import java.time.Instant;

import io.nixer.nixerplugin.stigma.StigmaConstants;
import io.nixer.nixerplugin.stigma.decision.StigmaService;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.login.StigmaCookieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 25/04/2020.
 *
 * @author Grzegorz Cwiak
 */
@ExtendWith(MockitoExtension.class)
class RevokedStigmaDetectingFilterTest {

    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken("stigma-token");

    private static final Stigma STIGMA = new Stigma("stigma-value");

    @Mock
    StigmaCookieService stigmaCookieService;

    @Mock
    StigmaService stigmaService;

    MockHttpServletRequest request = new MockHttpServletRequest();

    @InjectMocks
    RevokedStigmaDetectingFilter filter;

    @Test
    void should_mark_request_as_using_revoked_stigma() {
        // given
        given(stigmaCookieService.readStigmaToken(request)).willReturn(STIGMA_TOKEN);
        given(stigmaService.findStigmaDetails(STIGMA_TOKEN)).willReturn(stigmaDetails(StigmaStatus.REVOKED));

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(StigmaConstants.REVOKED_STIGMA_USAGE_ATTRIBUTE)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void should_mark_request_as_not_using_revoked_stigma() {
        // given
        given(stigmaCookieService.readStigmaToken(request)).willReturn(STIGMA_TOKEN);
        given(stigmaService.findStigmaDetails(STIGMA_TOKEN)).willReturn(stigmaDetails(StigmaStatus.ACTIVE));

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(StigmaConstants.REVOKED_STIGMA_USAGE_ATTRIBUTE)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void should_mark_request_as_not_using_revoked_stigma_when_stigma_not_present() {
        // given
        given(stigmaCookieService.readStigmaToken(request)).willReturn(null);
        given(stigmaService.findStigmaDetails(null)).willReturn(null);

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(StigmaConstants.REVOKED_STIGMA_USAGE_ATTRIBUTE)).isEqualTo(Boolean.FALSE);
    }

    private StigmaDetails stigmaDetails(final StigmaStatus status) {
        return new StigmaDetails(
                STIGMA,
                status,
                Instant.parse("2020-01-21T10:25:43.511Z")
        );
    }
}
package io.nixer.nixerplugin.stigma.detection;

import java.time.Instant;
import javax.servlet.http.HttpServletRequest;

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
class StigmaExtractingFilterTest {

    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken("stigma-token");

    private static final Stigma STIGMA = new Stigma("stigma-value");

    private static final StigmaDetails STIGMA_DETAILS = new StigmaDetails(
            STIGMA,
            StigmaStatus.ACTIVE,
            Instant.parse("2020-01-21T10:25:43.511Z"));

    @Mock
    StigmaCookieService stigmaCookieService;

    @Mock
    StigmaService stigmaService;

    HttpServletRequest request = new MockHttpServletRequest();

    @InjectMocks
    StigmaExtractingFilter filter;

    @Test
    void should_write_stigma_details_as_attribute() {
        // given
        given(stigmaCookieService.readStigmaToken(request)).willReturn(STIGMA_TOKEN);
        given(stigmaService.findStigmaDetails(STIGMA_TOKEN)).willReturn(STIGMA_DETAILS);

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(StigmaConstants.STIGMA_METADATA_ATTRIBUTE)).isEqualTo(STIGMA_DETAILS);
    }

    @Test
    void should_not_write_stigma_details_attribute_when_stigma_not_available() {
        // given
        given(stigmaCookieService.readStigmaToken(request)).willReturn(STIGMA_TOKEN);
        given(stigmaService.findStigmaDetails(STIGMA_TOKEN)).willReturn(null);

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(StigmaConstants.STIGMA_METADATA_ATTRIBUTE)).isNull();
    }
}
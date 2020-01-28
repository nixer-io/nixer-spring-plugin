package io.nixer.nixerplugin.stigma.evaluate;

import java.time.Instant;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaDetails;
import io.nixer.nixerplugin.stigma.storage.StigmaStorage;
import io.nixer.nixerplugin.stigma.generate.StigmaGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created on 2019-06-25.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class StigmaServiceTest {

    private static final Stigma STIGMA = new Stigma("stigma-value");

    private static final StigmaDetails STIGMA_DETAILS = new StigmaDetails(
            STIGMA,
            StigmaStatus.ACTIVE,
            Instant.parse("2020-01-21T10:25:43.511Z"));

    @Mock
    private StigmaStorage stigmaStorage;

    @Mock
    private StigmaGenerator stigmaGenerator;

    @InjectMocks
    private StigmaService stigmaService;

    @Test
    void should_find_stigma_details() {
        // given
        given(stigmaStorage.findStigmaDetails(STIGMA)).willReturn(STIGMA_DETAILS);

        // when
        final StigmaDetails result = stigmaService.findStigmaDetails(STIGMA);

        // then
        assertThat(result).isEqualTo(STIGMA_DETAILS);
        verify(stigmaStorage).recordStigmaObservation(STIGMA_DETAILS);
    }

    @Test
    void should_return_empty_result_when_stigma_not_found_in_storage() {
        // given
        given(stigmaStorage.findStigmaDetails(STIGMA)).willReturn(null);

        // when
        final StigmaDetails result = stigmaService.findStigmaDetails(STIGMA);

        // then
        assertThat(result).isNull();
        verify(stigmaStorage).recordSpottingUnknownStigma(STIGMA);
    }

    @Test
    void should_revoke_stigma() {
        // when
        stigmaService.revokeStigma(STIGMA);

        // then
        verify(stigmaStorage).updateStatus(STIGMA, StigmaStatus.REVOKED);
    }

    @Test
    void should_generate_new_stigma() {
        // given
        final Stigma stigma = new Stigma("new-stigma-value");
        final StigmaDetails stigmaDetails = new StigmaDetails(stigma, StigmaStatus.ACTIVE, Instant.parse("2020-01-22T11:26:44.512Z"));
        given(stigmaGenerator.newStigma()).willReturn(stigmaDetails);

        // when
        final StigmaDetails result = stigmaService.getNewStigma();

        // then
        assertThat(result).isEqualTo(stigmaDetails);
        verify(stigmaStorage).save(stigmaDetails);
    }
}

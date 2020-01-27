package io.nixer.nixerplugin.stigma.evaluate;

import java.time.Instant;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.stigma.token.StigmaValuesGenerator;
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
class StigmaTokenServiceTest {

    private static final Stigma STIGMA = new Stigma("stigma-value");

    private static final StigmaData STIGMA_DATA = new StigmaData(
            STIGMA,
            StigmaStatus.ACTIVE,
            Instant.parse("2020-01-21T10:25:43.511Z"));

    @Mock
    private StigmaTokenStorage stigmaTokenStorage;

    @Mock
    private StigmaValuesGenerator stigmaValuesGenerator;

    @InjectMocks
    private StigmaTokenService stigmaTokenService;

    @Test
    void should_find_stigma_data_for_valid_token() {
        // given
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(STIGMA_DATA);

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(STIGMA);

        // then
        assertThat(stigmaData).isEqualTo(STIGMA_DATA);
        verify(stigmaTokenStorage).recordStigmaObservation(STIGMA_DATA);
    }

    @Test
    void should_return_empty_result_when_stigma_not_found_in_storage() {
        // given
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(null);

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(STIGMA);

        // then
        assertThat(stigmaData).isNull();
        verify(stigmaTokenStorage).recordSpottingUnknownStigma(STIGMA);
    }

    @Test
    void should_revoke_stigma() {
        // when
        stigmaTokenService.revokeStigma(STIGMA);

        // then
        verify(stigmaTokenStorage).updateStatus(STIGMA, StigmaStatus.REVOKED);
    }

    @Test
    void should_generate_new_stigma_token() {
        // given
        final Stigma stigma = new Stigma("new-stigma-value");
        final StigmaData stigmaData = new StigmaData(stigma, StigmaStatus.ACTIVE, Instant.parse("2020-01-22T11:26:44.512Z"));
        given(stigmaValuesGenerator.newStigma()).willReturn(stigmaData);

        // when
        final StigmaData result = stigmaTokenService.getNewStigma();

        // then
        assertThat(result).isEqualTo(stigmaData);
        verify(stigmaTokenStorage).saveStigma(stigmaData);
    }
}

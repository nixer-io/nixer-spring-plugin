package io.nixer.nixerplugin.stigma.generate;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import io.nixer.nixerplugin.core.util.NowSource;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 28/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class StigmaGeneratorTest {

    private static final long STIGMA_VALUE = 1234567890L;
    private static final Instant NOW = LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(ZoneOffset.UTC);

    @Mock
    private SecureRandom random;

    @Mock
    private NowSource nowSource;

    @InjectMocks
    private StigmaGenerator stigmaGenerator;

    @Test
    void should_generate_new_stigma() {
        // given
        given(random.nextLong()).willReturn(STIGMA_VALUE);
        given(nowSource.now()).willReturn(NOW);

        // when
        final StigmaDetails result = stigmaGenerator.newStigma();

        // then
        assertThat(result).isEqualTo(
                new StigmaDetails(new Stigma(String.valueOf(STIGMA_VALUE)), StigmaStatus.ACTIVE, NOW)
        );
    }
}
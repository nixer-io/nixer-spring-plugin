package io.nixer.nixerplugin.core.stigma.evaluate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.util.NowSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 21/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class StigmaValidatorTest {

    private static final Instant NOW = LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(ZoneOffset.UTC);
    private static final Duration STIGMA_LIFETIME = Duration.ofDays(30);

    @Mock
    private NowSource nowSource;

    private StigmaValidator stigmaValidator;

    @BeforeEach
    void setUp() {
        given(nowSource.now()).willReturn(NOW);
        stigmaValidator = new StigmaValidator(nowSource, STIGMA_LIFETIME);
    }

    @Test
    void should_pass_validation() {
        // given
        final StigmaData stigmaData =
                new StigmaData(new Stigma("stigma-value"), StigmaStatus.ACTIVE, NOW.minus(STIGMA_LIFETIME));

        // when
        final boolean result = stigmaValidator.isValid(stigmaData);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidStigmaExamples")
    void should_fail_validation(final StigmaData invalidStigma) {
        // when
        final boolean result = stigmaValidator.isValid(invalidStigma);

        // then
        assertThat(result).isFalse();
    }

    static Stream<StigmaData> invalidStigmaExamples() {
        return Stream.of(
                new StigmaData(new Stigma("stigma-value"), StigmaStatus.REVOKED, NOW.minus(STIGMA_LIFETIME)), // revoked
                new StigmaData(new Stigma("stigma-value"), StigmaStatus.ACTIVE, NOW.minus(STIGMA_LIFETIME).minus(1, SECONDS)), // expired
                null
        );
    }
}
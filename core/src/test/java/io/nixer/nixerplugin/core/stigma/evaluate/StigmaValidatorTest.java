package io.nixer.nixerplugin.core.stigma.evaluate;

import java.util.stream.Stream;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 21/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class StigmaValidatorTest {

    private StigmaValidator stigmaValidator = new StigmaValidator();

    @Test
    void should_pass_validation() {
        // given
        final StigmaData stigmaData = new StigmaData(new Stigma("stigma-value"), StigmaStatus.ACTIVE);

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
                new StigmaData(new Stigma("stigma-value"), StigmaStatus.REVOKED),
                null
        );
    }
}
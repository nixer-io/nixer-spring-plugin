package io.nixer.nixerplugin.pwned.check;

import com.google.common.base.Strings;
import io.nixer.bloom.check.BloomFilterCheck;
import io.nixer.nixerplugin.core.metrics.MetricsCounter;
import io.nixer.nixerplugin.pwned.metrics.PwnedPasswordMetricsReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Created on 11/10/2019.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class PwnedCredentialsCheckerTest {

    private static final String SAMPLE_PASSWORD = "samplePassword";
    private static final int MAX_PASSWORD_LENGTH = 50;

    @Mock
    private BloomFilterCheck pwnedFilter;

    private PwnedCredentialsChecker pwnedCredentialsChecker;

    @BeforeEach
    void setUp() {
        pwnedCredentialsChecker = new PwnedCredentialsChecker(
                pwnedFilter,
                MAX_PASSWORD_LENGTH,
                new MetricsReporterStub()
        );
    }

    @Test
    void shouldDetectPwnedPassword() {
        // given
        given(pwnedFilter.test(SAMPLE_PASSWORD)).willReturn(true);

        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(SAMPLE_PASSWORD);

        // then
        assertThat(result).isTrue();
        verify(pwnedFilter).test(SAMPLE_PASSWORD);
    }

    @Test
    void shouldDetectNotPwnedPassword() {
        // given
        given(pwnedFilter.test(SAMPLE_PASSWORD)).willReturn(false);

        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(SAMPLE_PASSWORD);

        // then
        assertThat(result).isFalse();
        verify(pwnedFilter).test(SAMPLE_PASSWORD);
    }

    @Test
    void shouldSkipCheckForMissingPassword() {
        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(null);

        // then
        assertThat(result).isFalse();
        verifyNoInteractions(pwnedFilter);
    }

    @Test
    void shouldSkipCheckForTooLongPassword() {
        // given
        final String tooLongPassword = Strings.repeat("a", MAX_PASSWORD_LENGTH + 1);

        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(tooLongPassword);

        // then
        assertThat(result).isFalse();
        verifyNoInteractions(pwnedFilter);
    }

    private static class MetricsReporterStub extends PwnedPasswordMetricsReporter {
        private static final MetricsCounter NOP_COUNTER = () -> {
        };

        MetricsReporterStub() {
            super(NOP_COUNTER, NOP_COUNTER);
        }
    }
}

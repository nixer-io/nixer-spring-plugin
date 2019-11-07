package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.core.metrics.MetricsCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created on 18/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class PwnedPasswordActiveCounterReporterTest {

    @Mock
    private MetricsCounter pwnedPasswordCounter;

    @Mock
    private MetricsCounter notPwnedPasswordCounter;

    private PwnedPasswordMetricsReporter metricsReporter;

    @BeforeEach
    void setup() {
        metricsReporter = new PwnedPasswordMetricsReporter(pwnedPasswordCounter, notPwnedPasswordCounter);
    }

    @Test
    void shouldForwardActionResultAndReportMetricsForPwnedPassword() {
        // when
        final Boolean result = metricsReporter.report(this::pwnedPassword);

        // then
        assertThat(result).isTrue();
        verify(pwnedPasswordCounter).increment();
    }

    @Test
    void shouldForwardActionResultAndReportMetricsForNotPwnedPassword() {
        // when
        final Boolean result = metricsReporter.report(this::notPwnedPassword);

        // then
        assertThat(result).isFalse();
        verify(notPwnedPasswordCounter).increment();
    }

    private Boolean pwnedPassword() {
        return true;
    }

    private Boolean notPwnedPassword() {
        return false;
    }
}
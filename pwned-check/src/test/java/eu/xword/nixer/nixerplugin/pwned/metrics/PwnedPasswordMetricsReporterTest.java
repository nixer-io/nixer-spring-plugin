package eu.xword.nixer.nixerplugin.pwned.metrics;

import java.util.function.Supplier;

import eu.xword.nixer.nixerplugin.core.metrics.MetricsWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class PwnedPasswordMetricsReporterTest {

    @Mock
    MetricsWriter metricsWriter;

    @InjectMocks
    PwnedPasswordMetricsReporter pwnedPasswordMetricsReporter;

    @Test
    void shouldForwardActionResultAndReportMetricsForPwnedPassword() {
        // given
        final Supplier<Boolean> pwnedPasswordRevealingAction = () -> true;

        // when
        final Boolean result = pwnedPasswordMetricsReporter.report(pwnedPasswordRevealingAction);

        // then
        assertThat(result).isTrue();
        verify(metricsWriter).write(PwnedCheckMetrics.PWNED_PASSWORD);
    }

    @Test
    void shouldForwardActionResultAndReportMetricsForNotPwnedPassword() {
        // given
        final Supplier<Boolean> notPwnedPasswordRevealingAction = () -> false;

        // when
        final Boolean result = pwnedPasswordMetricsReporter.report(notPwnedPasswordRevealingAction);

        // then
        assertThat(result).isFalse();
        verify(metricsWriter).write(PwnedCheckMetrics.NOT_PWNED_PASSWORD);
    }
}
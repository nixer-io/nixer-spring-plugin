package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.core.metrics.MetricsCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaptchaCountersReporterTest {

    @Mock
    private MetricsCounter passedCaptchaCounter;

    @Mock
    private MetricsCounter failedCaptchaCounter;

    private CaptchaMetricsReporter metricsReporter;

    @BeforeEach
    void setup() {
        metricsReporter = new CaptchaMetricsReporter(passedCaptchaCounter, failedCaptchaCounter);
    }

    @Test
    void shouldIncrementPassCounter() {
        metricsReporter.reportPassedCaptcha();

        verify(passedCaptchaCounter).increment();
        verify(failedCaptchaCounter, never()).increment();
    }

    @Test
    void shouldIncrementFailedCounter() {
        metricsReporter.reportFailedCaptcha();

        verify(failedCaptchaCounter).increment();
        verify(passedCaptchaCounter, never()).increment();
    }
}
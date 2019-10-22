package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static eu.xword.nixer.nixerplugin.captcha.config.CaptchaMetricsProperties.DEFAULT;

public class MetricsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "nixer.captcha.metrics", name = "enabled", havingValue = "true", matchIfMissing = DEFAULT)
    public MetricsReporterFactory metricsReporterFactory(MeterRegistry meterRegistry) {
        return new MicrometerMetricsReporterFactory(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "nixer.captcha.metrics", name = "enabled", havingValue = "false")
    public MetricsReporterFactory nopMetricsReporterFactory() {
        return new NOPMetricsFactory();
    }

    public static final class NOPMetricsFactory implements MetricsReporterFactory {

        @Override
        public MetricsReporter createMetricsReporter(final String action) {
            return new NOPMetricsReporter();
        }
    }
}

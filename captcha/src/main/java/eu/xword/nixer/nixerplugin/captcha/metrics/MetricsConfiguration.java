package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.captcha.config.CaptchaProperties.MetricsProperties.DEFAULT;

@Configuration
public class MetricsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "captcha.metrics", name = "enabled", havingValue = "true", matchIfMissing = DEFAULT)
    public MetricsReporterFactory metricsReporterFactory(MeterRegistry meterRegistry) {
        return new MicrometerMetricsReporterFactory(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "captcha.metrics", name = "enabled", havingValue = "false")
    public MetricsReporterFactory nopMetricsReporterFactory() {
        return action -> new NOPMetricsReporter();
    }

}

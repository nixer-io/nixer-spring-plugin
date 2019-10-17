package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.MeterDefinition;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacade;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacadeWriter;
import eu.xword.nixer.nixerplugin.metrics.NOPMetricsWriter;
import eu.xword.nixer.nixerplugin.pwned.PwnedCheckAutoConfiguration;
import io.micrometer.core.instrument.Counter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
@Configuration
@ConditionalOnBean(PwnedCheckAutoConfiguration.class)
@EnableConfigurationProperties(value = {PwnedCheckMetricsProperties.class})
public class PwnedCheckMetricsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "nixer.pwned.check.metrics.enabled", havingValue = "false")
    public PwnedCheckMetricsWriterFactory nopMetricsWriterFactory() {
        return NOPMetricsWriter::new;
    }

    @Bean
    @ConditionalOnProperty(value = "nixer.pwned.check.metrics.enabled")
    public PwnedCheckMetricsWriterFactory pwnedCheckMetricsWriterFactory(final MetricsFacade metricsFacade) {
        return () -> new MetricsFacadeWriter(metricsFacade);
    }

    @Configuration
    @ConditionalOnProperty(value = "nixer.pwned.check.metrics.enabled")
    public static class MetersConfiguration {

        @Bean
        public MeterDefinition pwnedPasswordCounter() {
            return MeterDefinition.counter(
                    "pwned_password_positive",
                    () -> Counter.builder("pwned_password")
                            .description("Password is pwned")
                            .tag("result", "positive")
            );
        }

        @Bean
        public MeterDefinition notPwnedPasswordCounter() {
            return MeterDefinition.counter(
                    "pwned_password_negative",
                    () -> Counter.builder("pwned_password")
                            .description("Password is not pwned")
                            .tag("result", "negative")
            );
        }
    }
}

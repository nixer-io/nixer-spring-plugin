package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.MetersRepository;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacade;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacadeWriter;
import eu.xword.nixer.nixerplugin.metrics.MetricsWriterFactory;
import eu.xword.nixer.nixerplugin.metrics.NOPMetricsWriter;
import eu.xword.nixer.nixerplugin.pwned.PwnedCheckAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.NOT_PWNED_PASSWORD;
import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.PWNED_PASSWORD;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
@Configuration
@ConditionalOnBean(PwnedCheckAutoConfiguration.class)
@EnableConfigurationProperties(value = {PwnedCheckMetricsProperties.class})
public class PwnedCheckMetricsConfiguration {

    @Bean("pwnedCheckMetricsWriterFactory")
    public MetricsWriterFactory pwnedCheckMetricsWriterFactory(@Value("${nixer.pwned.check.metrics.enabled}") final boolean pwnedMetricsEnabled,
                                                               final MetricsFacade metricsFacade) {
        return pwnedMetricsEnabled
                ? () -> new MetricsFacadeWriter(metricsFacade)
                : NOPMetricsWriter::new;
    }

    @Bean
    public PwnedPasswordMetricsReporter pwnedPasswordMetricsReporter(
            @Qualifier("pwnedCheckMetricsWriterFactory") MetricsWriterFactory metricsWriterFactory) {

        return new PwnedPasswordMetricsReporter(metricsWriterFactory.createMetricsWriter());
    }

    @Bean
    @ConditionalOnProperty(value = "nixer.pwned.check.metrics.enabled")
    public MetersRepository.Contributor pwnedCheckMetersConfigurer() {

        return builder -> {
            builder.register(PWNED_PASSWORD);
            builder.register(NOT_PWNED_PASSWORD);
        };
    }
}

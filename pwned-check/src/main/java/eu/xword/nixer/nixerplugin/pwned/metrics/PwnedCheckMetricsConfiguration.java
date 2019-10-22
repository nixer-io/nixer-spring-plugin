package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.MetersRepository;
import eu.xword.nixer.nixerplugin.metrics.MetricsWriterFactory;
import eu.xword.nixer.nixerplugin.metrics.MicrometerMetricsWriter;
import eu.xword.nixer.nixerplugin.metrics.NOPMetricsWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class PwnedCheckMetricsConfiguration {

    @Bean("pwnedCheckMetricsWriterFactory")
    public MetricsWriterFactory pwnedCheckMetricsWriterFactory(@Value("${nixer.pwned.check.metrics.enabled}") boolean pwnedMetricsEnabled,
                                                               final MetersRepository metersRepository) {
        return pwnedMetricsEnabled
                ? () -> new MicrometerMetricsWriter(metersRepository)
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

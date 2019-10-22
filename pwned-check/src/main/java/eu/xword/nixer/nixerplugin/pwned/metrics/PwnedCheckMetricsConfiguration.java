package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.MetersRepository;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacade;
import eu.xword.nixer.nixerplugin.metrics.MetricsFacadeWriter;
import eu.xword.nixer.nixerplugin.metrics.NOPMetricsWriter;
import eu.xword.nixer.nixerplugin.pwned.PwnedCheckAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @Bean // TODO Fix IntelliJ incorrectly complaining about two PwnedCheckMetricsWriterFactory beans detected.
    public PwnedPasswordMetricsReporter pwnedPasswordMetricsReporter(PwnedCheckMetricsWriterFactory metricsWriterFactory) {
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

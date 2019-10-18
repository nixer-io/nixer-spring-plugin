package eu.xword.nixer.nixerplugin.metrics;

import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 16/10/2019.
 *
 * @author gcwiak
 */
// FIXME get rid of the explicit bean name after resolving conflict with the captcha MetricsConfiguration bean
@Configuration("genericMetricsConfiguration")
public class MetricsConfiguration {

    @Bean
    public MetersRepository metersRepository(final List<MetersRepository.Contributor> contributors, final MeterRegistry meterRegistry) {
        final MetersRepository.Builder builder = new MetersRepository.Builder();

        contributors.forEach(contributor -> contributor.contribute(builder));

        return builder.build(meterRegistry);
    }
}

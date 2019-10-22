package eu.xword.nixer.nixerplugin.core.metrics;

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
        return MetersRepository.build(contributors, meterRegistry);
    }
}

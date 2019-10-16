package eu.xword.nixer.nixerplugin.metrics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Meter;
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
    public MetricsFacade metricsFacade(final List<MeterDefinition> meterDefinitions, final MeterRegistry meterRegistry) {

        // TODO move this transformation to a dedicated component
        final Map<String, Meter> meters = meterDefinitions.stream()
                .collect(Collectors.toMap(
                        MeterDefinition::getLookupId,
                        meterDefinition -> meterDefinition.register(meterRegistry)
                ));

        return new MetricsFacade(meters);
    }
}

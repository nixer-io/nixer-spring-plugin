package io.nixer.nixerplugin.core.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    public MetricsFactory metricsFactory(MeterRegistry meterRegistry) {
        return MetricsFactory.create(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(MeterRegistry.class)
    public MetricsFactory nullMetricsFactory() {
        return MetricsFactory.createNullFactory();
    }
}

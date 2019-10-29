package eu.xword.nixer.nixerplugin.core.metrics;

import java.util.List;

import eu.xword.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import eu.xword.nixer.nixerplugin.core.login.metrics.LoginMetricsReporter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class MetricsConfiguration {

    @Bean
    public LoginMetricsReporter loginMetricsReporter(MetricsFactory metricsFactory) {
        return new LoginMetricsReporter(metricsFactory);
    }

    @Bean
    public LoginFailureTypeRegistry loginFailuresRegistry(List<LoginFailureTypeRegistry.Contributor> consumers) {
        final LoginFailureTypeRegistry.Builder builder = LoginFailureTypeRegistry.builder();

        consumers.forEach(builderConsumer -> builderConsumer.contribute(builder));

        return builder.build();
        //todo move somewhere else
    }

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

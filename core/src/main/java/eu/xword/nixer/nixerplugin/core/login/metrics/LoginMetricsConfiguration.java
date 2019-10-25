package eu.xword.nixer.nixerplugin.core.login.metrics;

import java.util.List;

import eu.xword.nixer.nixerplugin.core.login.LoginFailureTypeRegistry;
import eu.xword.nixer.nixerplugin.core.login.LoginMetricsProperties;
import eu.xword.nixer.nixerplugin.core.metrics.MetersRepository;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsWriterFactory;
import eu.xword.nixer.nixerplugin.core.metrics.MicrometerMetricsWriter;
import eu.xword.nixer.nixerplugin.core.metrics.NOPMetricsWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = LoginMetricsProperties.class)
public class LoginMetricsConfiguration {

    @Bean("loginMetricsWriterFactory")
    public MetricsWriterFactory loginMetricsWriterFactory(@Value("${nixer.login.metrics.enabled}") boolean metricsEnabled,
                                                          final MetersRepository metersRepository) {
        return metricsEnabled
                ? () -> new MicrometerMetricsWriter(metersRepository)
                : NOPMetricsWriter::new;
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.login.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(LoginMetricsReporter.class)
    public LoginMetricsReporter loginMetricsReporter(@Qualifier("loginMetricsWriterFactory") MetricsWriterFactory metricsWriterFactor) {
        return new LoginMetricsReporter(metricsWriterFactor.createMetricsWriter());
    }

    @Bean
    @ConditionalOnProperty(prefix = "nixer.login.metrics", name = "enabled")
    public MetersRepository.Contributor loginMetersConfigurer() {

        return builder -> {
            for (LoginMetrics metric : LoginMetrics.values()) {
                builder.register(metric);
            }
        };
    }

    @Bean
    public LoginFailureTypeRegistry loginFailuresRegistry(List<LoginFailureTypeRegistry.Contributor> consumers) {
        final LoginFailureTypeRegistry.Builder builder = LoginFailureTypeRegistry.builder();

        consumers.forEach(builderConsumer -> builderConsumer.contribute(builder));

        return builder.build();

    }

}

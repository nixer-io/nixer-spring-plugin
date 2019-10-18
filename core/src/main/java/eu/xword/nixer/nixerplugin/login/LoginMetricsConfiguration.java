package eu.xword.nixer.nixerplugin.login;

import java.util.List;

import eu.xword.nixer.nixerplugin.login.metrics.LoginMetricsReporter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = LoginMetricsProperties.class)
public class LoginMetricsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "nixer.login.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(LoginMetricsReporter.class)
//    @ConditionalOnBean(MeterRegistry.class) // TODO making this active breaks  program causing bean not being registered
    public LoginMetricsReporter loginMetrics(MeterRegistry meterRegistry, LoginFailureTypeRegistry failureTypeRegistry) {
        return new LoginMetricsReporter(meterRegistry, failureTypeRegistry);
    }

    @Bean
    public LoginFailureTypeRegistry loginFailuresRegistry(List<LoginFailureTypeRegistry.Contributor> consumers) {
        final LoginFailureTypeRegistry.Builder builder = LoginFailureTypeRegistry.builder();

        consumers.forEach(builderConsumer -> builderConsumer.contribute(builder));

        return builder.build();

    }

}

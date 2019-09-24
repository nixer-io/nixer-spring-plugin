package eu.xword.nixer.nixerplugin.login;

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
    public LoginActivityRepository loginMetrics(MeterRegistry meterRegistry) {
        return new LoginMetricsReporter(meterRegistry);
    }

}

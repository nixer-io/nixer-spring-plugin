package eu.xword.nixer.nixerplugin.core.login;

import java.util.List;

import eu.xword.nixer.nixerplugin.core.login.metrics.LoginMetricsReporter;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.context.annotation.Bean;

public class LoginConfiguration {

    @Bean
    public LoginMetricsReporter loginMetricsReporter(MetricsFactory metricsFactory) {
        return new LoginMetricsReporter(metricsFactory);
    }

    @Bean
    public LoginFailureTypeRegistry loginFailuresRegistry(List<LoginFailureTypeRegistry.Contributor> consumers) {
        final LoginFailureTypeRegistry.Builder builder = LoginFailureTypeRegistry.builder();

        consumers.forEach(builderConsumer -> builderConsumer.contribute(builder));

        return builder.build();
    }
}

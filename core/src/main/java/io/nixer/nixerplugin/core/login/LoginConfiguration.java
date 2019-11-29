package io.nixer.nixerplugin.core.login;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import io.nixer.nixerplugin.core.login.metrics.LoginMetricsReporter;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    @Bean
    public LoginActivityService loginActivityService(List<LoginActivityRepository> loginActivityRepositories,
                                                     AnomalyRulesRunner anomalyRulesRunner) {
        return new LoginActivityService(loginActivityRepositories, anomalyRulesRunner);
    }

    @Bean
    public LoginContextFactory loginContextFactory(HttpServletRequest httpServletRequest,
                                                   LoginFailureTypeRegistry loginFailureTypeRegistry) {

        return new LoginContextFactory(httpServletRequest, loginFailureTypeRegistry);
    }

    @Bean
    public LoginActivityListener loginActivityListener(LoginContextFactory loginContextFactory,
                                                       List<LoginActivityHandler> loginActivityHandlers) {

        return new LoginActivityListener(loginContextFactory, loginActivityHandlers);
    }
}

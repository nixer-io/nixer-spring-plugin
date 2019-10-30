package eu.xword.nixer.nixerplugin.core.login;

import java.util.List;
import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.core.detection.rules.AnomalyRulesRunner;
import eu.xword.nixer.nixerplugin.core.login.jdbc.JdbcDAOConfigurer;
import eu.xword.nixer.nixerplugin.core.login.metrics.LoginMetricsReporter;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsFactory;
import eu.xword.nixer.nixerplugin.core.stigma.StigmaService;
import eu.xword.nixer.nixerplugin.core.stigma.StigmaUtils;
import org.springframework.context.annotation.Bean;

public class LoginConfiguration {

    @Bean
    public JdbcDAOConfigurer JdbcDAOConfigurer(DataSource dataSource) {
        return new JdbcDAOConfigurer(dataSource);
    }

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
    public LoginActivityListener loginActivityListener(StigmaService stigmaService,
                                                       StigmaUtils stigmaUtils,
                                                       LoginActivityService loginActivityService,
                                                       LoginFailureTypeRegistry loginFailureTypeRegistry) {
        return new LoginActivityListener(
                stigmaService,
                stigmaUtils,
                loginActivityService,
                loginFailureTypeRegistry
        );
    }
}

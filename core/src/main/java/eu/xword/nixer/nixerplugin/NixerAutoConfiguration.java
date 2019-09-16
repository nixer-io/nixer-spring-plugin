package eu.xword.nixer.nixerplugin;

import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import eu.xword.nixer.nixerplugin.filter.FilterConfiguration;
import eu.xword.nixer.nixerplugin.login.jdbc.JdbcDAO;
import eu.xword.nixer.nixerplugin.login.metrics.LoginMetricsReporter;
import eu.xword.nixer.nixerplugin.stigma.StigmaConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;

@EnableConfigurationProperties({NixerProperties.class})
@Configuration
@Import(value = {FilterConfiguration.class, StigmaConfiguration.class})
public class NixerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "nixer.login.enable-metrics", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(LoginMetricsReporter.class)
//    @ConditionalOnBean(MeterRegistry.class) // TODO making this active breaks  program causing bean not being registered
    public LoginActivityRepository loginMetrics(MeterRegistry meterRegistry) {
        return new LoginMetricsReporter(meterRegistry);
    }

    @Bean
    public JdbcDAO jdbcDAO(DataSource dataSource) {
        final JdbcDAO jdbcDAO = new JdbcDAO();
        jdbcDAO.setDataSource(dataSource);
        return jdbcDAO;
    }

    @Bean
    public GlobalCredentialStuffing credentialStuffing() {
        return new GlobalCredentialStuffing();
    }

}

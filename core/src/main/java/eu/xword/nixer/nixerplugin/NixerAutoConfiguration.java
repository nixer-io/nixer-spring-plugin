package eu.xword.nixer.nixerplugin;

import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.blocking.BlockingConfiguration;
import eu.xword.nixer.nixerplugin.login.jdbc.JdbcDAO;
import eu.xword.nixer.nixerplugin.login.metrics.LoginMetricsReporter;
import eu.xword.nixer.nixerplugin.stigma.StigmaConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({NixerProperties.class})
@Configuration
@Import(value = {BlockingConfiguration.class, StigmaConfiguration.class})
public class NixerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LoginMetricsReporter.class)
    @ConditionalOnClass(MeterRegistry.class)
    public LoginMetricsReporter loginMetrics(MeterRegistry meterRegistry) {
        return new LoginMetricsReporter(meterRegistry);
    }

    @Bean
    public JdbcDAO jdbcDAO(DataSource dataSource) {
        final JdbcDAO jdbcDAO = new JdbcDAO();
        jdbcDAO.setDataSource(dataSource);
        return jdbcDAO;
    }
}

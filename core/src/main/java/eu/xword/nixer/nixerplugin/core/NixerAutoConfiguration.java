package eu.xword.nixer.nixerplugin.core;

import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.core.detection.config.DetectionConfiguration;
import eu.xword.nixer.nixerplugin.core.events.elastic.ElasticLoggingAutoConfiguration;
import eu.xword.nixer.nixerplugin.core.filter.FilterConfiguration;
import eu.xword.nixer.nixerplugin.core.ip.IpFilterConfiguration;
import eu.xword.nixer.nixerplugin.core.login.jdbc.JdbcDAO;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsConfiguration;
import eu.xword.nixer.nixerplugin.core.registry.GlobalCredentialStuffingRegistry;
import eu.xword.nixer.nixerplugin.core.stigma.StigmaConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static eu.xword.nixer.nixerplugin.core.NixerAutoConfiguration.ORDER;

@Configuration
@ComponentScan
@AutoConfigureOrder(ORDER)
@Import({
        DetectionConfiguration.class,
        FilterConfiguration.class,
        MetricsConfiguration.class,
        StigmaConfiguration.class,
        IpFilterConfiguration.class,
        ElasticLoggingAutoConfiguration.class
})
public class NixerAutoConfiguration {

    public static final int ORDER = 10;

    @Bean
    public JdbcDAO jdbcDAO(DataSource dataSource) {
        final JdbcDAO jdbcDAO = new JdbcDAO();
        jdbcDAO.setDataSource(dataSource);
        return jdbcDAO;
    }

    @Bean
    public GlobalCredentialStuffingRegistry credentialStuffing() {
        return new GlobalCredentialStuffingRegistry();
    }

}

package eu.xword.nixer.nixerplugin.core;

import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.core.login.jdbc.JdbcDAO;
import eu.xword.nixer.nixerplugin.core.registry.GlobalCredentialStuffingRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class NixerAutoConfiguration {

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

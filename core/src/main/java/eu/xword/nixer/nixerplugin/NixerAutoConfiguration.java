package eu.xword.nixer.nixerplugin;

import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.registry.GlobalCredentialStuffingRegistry;
import eu.xword.nixer.nixerplugin.filter.FilterConfiguration;
import eu.xword.nixer.nixerplugin.login.jdbc.JdbcDAO;
import eu.xword.nixer.nixerplugin.stigma.StigmaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {FilterConfiguration.class, StigmaConfiguration.class})
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

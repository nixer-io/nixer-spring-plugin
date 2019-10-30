package eu.xword.nixer.nixerplugin.core.stigma.storage;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import eu.xword.nixer.nixerplugin.core.login.jdbc.JdbcDAO;
import org.springframework.util.Assert;

public class JdbcStigmaRepository implements StigmaRepository {

    private final JdbcDAO jdbcDAO;

    public JdbcStigmaRepository(final JdbcDAO jdbcDAO) {
        Assert.notNull(jdbcDAO, "JdbcDAO must not be null");
        this.jdbcDAO = jdbcDAO;
    }

    @Override
    public void save(final String stigma, final LoginResult loginResult) {
        jdbcDAO.save(stigma);
    }
}
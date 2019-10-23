package eu.xword.nixer.nixerplugin.core.stigma.storage;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;
import eu.xword.nixer.nixerplugin.core.login.jdbc.JdbcDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcStigmaRepository implements StigmaRepository {

    @Autowired
    private JdbcDAO jdbcDAO;

    @Override
    public void save(final String stigma, final LoginResult loginResult) {
        jdbcDAO.save(stigma);
    }
}
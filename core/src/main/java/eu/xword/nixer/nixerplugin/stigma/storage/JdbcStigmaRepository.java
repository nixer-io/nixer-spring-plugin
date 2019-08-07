package eu.xword.nixer.nixerplugin.stigma.storage;

import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.jdbc.JdbcDAO;
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
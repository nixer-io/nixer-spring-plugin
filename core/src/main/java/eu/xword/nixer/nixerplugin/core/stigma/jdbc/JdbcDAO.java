package eu.xword.nixer.nixerplugin.core.stigma.jdbc;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JdbcDAO extends JdbcDaoSupport {

    private static final String INSERT_UPDATE = "INSERT INTO stigma_tokens (value) VALUES (?)";


    public void save(String stigma) {
        getJdbcTemplate().update(INSERT_UPDATE, stigma);
    }
}

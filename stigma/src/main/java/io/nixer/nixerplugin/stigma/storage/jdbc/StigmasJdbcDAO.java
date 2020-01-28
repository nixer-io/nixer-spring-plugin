package io.nixer.nixerplugin.stigma.storage.jdbc;

import java.util.List;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StigmasJdbcDAO extends JdbcDaoSupport {

    private static final RowMapper<StigmaDetails> STIGMA_DETAILS_MAPPER = (rs, rowNum) -> new StigmaDetails(
            new Stigma(rs.getString("stigma_value")),
            StigmaStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("creation_date").toInstant()
    );

    public int create(final StigmaDetails stigmaDetails) {
        return getJdbcTemplate().update(
                "INSERT INTO stigmas (stigma_value, status, creation_date) VALUES (?, ?, ?)",
                stigmaDetails.getStigma().getValue(), stigmaDetails.getStatus().name(), stigmaDetails.getCreationDate()
        );
    }

    public StigmaDetails findStigmaDetails(final Stigma stigma) {
        return getJdbcTemplate().queryForObject(
                "SELECT * FROM stigmas WHERE stigma_value = ?",
                new Object[]{stigma.getValue()},
                STIGMA_DETAILS_MAPPER
        );
    }

    public int updateStatus(final Stigma stigma, final StigmaStatus newStatus) {
        return getJdbcTemplate().update(
                "UPDATE stigmas SET status = ? WHERE stigma_value = ?",
                newStatus.name(), stigma.getValue()
        );
    }

    public List<StigmaDetails> getAll() {
        return getJdbcTemplate().query("SELECT * FROM stigmas", STIGMA_DETAILS_MAPPER);
    }
}

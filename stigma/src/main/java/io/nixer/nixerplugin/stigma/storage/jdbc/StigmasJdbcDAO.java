package io.nixer.nixerplugin.stigma.storage.jdbc;

import java.util.List;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StigmasJdbcDAO extends JdbcDaoSupport {

    private static final RowMapper<StigmaData> STIGMA_DATA_MAPPER = (rs, rowNum) -> new StigmaData(
            new Stigma(rs.getString("stigma_value")),
            StigmaStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("creation_date").toInstant()
    );

    public int create(final StigmaData stigmaData) {
        return getJdbcTemplate().update(
                "INSERT INTO stigmas (stigma_value, status, creation_date) VALUES (?, ?, ?)",
                stigmaData.getStigma().getValue(), stigmaData.getStatus().name(), stigmaData.getCreationDate()
        );
    }

    public StigmaData findStigmaData(final Stigma stigma) {
        return getJdbcTemplate().queryForObject(
                "SELECT * FROM stigmas WHERE stigma_value = ?",
                new Object[]{stigma.getValue()},
                STIGMA_DATA_MAPPER
        );
    }

    public int updateStatus(final Stigma stigma, final StigmaStatus newStatus) {
        return getJdbcTemplate().update(
                "UPDATE stigmas SET status = ? WHERE stigma_value = ?",
                newStatus.name(), stigma.getValue()
        );
    }

    public List<StigmaData> getAll() {
        return getJdbcTemplate().query("SELECT * FROM stigmas", STIGMA_DATA_MAPPER);
    }
}

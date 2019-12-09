package io.nixer.nixerplugin.core.stigma.storage.jdbc;

import java.util.List;
import java.util.UUID;

import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StigmasJdbcDAO extends JdbcDaoSupport {

    private static final RowMapper<StigmaData> STIGMA_DATA_MAPPER = (rs, rowNum) -> new StigmaData(
            UUID.fromString(rs.getString("guid")),
            rs.getString("stigma_value"),
            StigmaStatus.valueOf(rs.getString("status"))
    );

    public void create(final StigmaData stigmaData) {
        getJdbcTemplate().update(
                "INSERT INTO stigmas (guid, stigma_value, status) VALUES (?, ?, ?)",
                stigmaData.getGuid(), stigmaData.getStigmaValue(), stigmaData.getStatus().name()
        );
    }

    public StigmaData findByStigmaValue(final String stigmaValue) {
        return getJdbcTemplate().queryForObject(
                "SELECT * FROM stigmas WHERE stigma_value = ?",
                new Object[]{stigmaValue},
                STIGMA_DATA_MAPPER
        );
    }

    public void updateStigmaStatus(final String stigmaValue, final StigmaStatus status) {
        getJdbcTemplate().update(
                "UPDATE stigmas SET status = ? WHERE stigma_value = ?",
                status.name(), stigmaValue
        );
    }

    public List<StigmaData> getAll() {

        return getJdbcTemplate().query(
                "SELECT * FROM stigmas",
                STIGMA_DATA_MAPPER
        );
    }
}

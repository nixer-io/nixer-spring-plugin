package io.nixer.nixerplugin.stigma.storage.jdbc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.storage.StigmaTokenStorage;
import org.springframework.util.Assert;

/**
 * Created on 05/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmasJdbcStorage implements StigmaTokenStorage {

    private final StigmasJdbcDAO stigmasDAO;

    public StigmasJdbcStorage(final StigmasJdbcDAO stigmasDAO) {
        this.stigmasDAO = stigmasDAO;
    }

    @Override
    public void saveStigma(@Nonnull final Stigma stigma, @Nonnull final StigmaStatus status) {
        final int created = stigmasDAO.create(new StigmaData(stigma, status));
        Assert.state(created == 1, () -> "Expected to create exactly one entry but was: " + created);
    }

    @Nullable
    @Override
    public StigmaData findStigmaData(@Nonnull final Stigma stigma) {
        return stigmasDAO.findStigmaData(stigma);
    }

    @Override
    public void updateStatus(@Nonnull final Stigma stigma, final StigmaStatus newStatus) {
        final int updated = stigmasDAO.updateStatus(stigma, newStatus);
        Assert.state(updated == 1, () -> "Expected to update exactly one entry but was: " + updated);
    }
}

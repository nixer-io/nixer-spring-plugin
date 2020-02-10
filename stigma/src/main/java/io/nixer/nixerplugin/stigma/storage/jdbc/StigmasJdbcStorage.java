package io.nixer.nixerplugin.stigma.storage.jdbc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.storage.StigmaStorage;
import org.springframework.util.Assert;

/**
 * Created on 05/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmasJdbcStorage implements StigmaStorage {

    private final StigmasJdbcDAO stigmasDAO;

    public StigmasJdbcStorage(final StigmasJdbcDAO stigmasDAO) {
        this.stigmasDAO = stigmasDAO;
    }

    @Override
    public void save(@Nonnull final StigmaDetails stigmaDetails) {
        final int created = stigmasDAO.create(stigmaDetails);
        Assert.state(created == 1, () -> "Expected to create exactly one entry but was: " + created);
    }

    @Nullable
    @Override
    public StigmaDetails findStigmaDetails(@Nonnull final Stigma stigma) {
        return stigmasDAO.findStigmaDetails(stigma);
    }

    @Override
    public void updateStatus(@Nonnull final Stigma stigma, final StigmaStatus newStatus) {
        final int updated = stigmasDAO.updateStatus(stigma, newStatus);
        Assert.state(updated == 1, () -> "Expected to update exactly one entry but was: " + updated);
    }
}

package io.nixer.nixerplugin.core.stigma.storage.jdbc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaTokenStorage;

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

    @Nonnull
    @Override
    public Stigma createStigma(@Nonnull final Stigma stigma, @Nonnull final  StigmaStatus status) {
        stigmasDAO.create(new StigmaData(stigma, status));

        // FIXME return more reasonable object
        return stigma;
    }

    @Nullable
    @Override
    public StigmaData findStigmaData(@Nonnull final Stigma stigma) {
        return stigmasDAO.findStigmaData(stigma);
    }

    @Override
    public void revokeStigma(@Nonnull final Stigma stigma) {
        stigmasDAO.updateStigmaStatus(stigma, StigmaStatus.REVOKED);
    }
}

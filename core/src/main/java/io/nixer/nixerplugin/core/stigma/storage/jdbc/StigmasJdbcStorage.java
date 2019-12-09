package io.nixer.nixerplugin.core.stigma.storage.jdbc;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.evaluate.Stigma;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaStatus;
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
    public Stigma createStigma(String stigmaValue, final StigmaStatus status) {
        stigmasDAO.create(
                new StigmaData(
                        UUID.randomUUID(), stigmaValue, status
                )
        );

        // FIXME return more reasonable object
        return new Stigma(stigmaValue);
    }

    @Nullable
    @Override
    public StigmaData findStigmaData(@Nonnull final Stigma stigma) {
        return stigmasDAO.findByStigmaValue(stigma.getValue());
    }

    @Override
    public void revokeStigma(@Nonnull final String stigmaValue) {
        stigmasDAO.updateStigmaStatus(stigmaValue, StigmaStatus.REVOKED);
    }

    @Override
    public void recordSpottingUnknownStigma(@Nonnull final Stigma stigma) {

    }
}

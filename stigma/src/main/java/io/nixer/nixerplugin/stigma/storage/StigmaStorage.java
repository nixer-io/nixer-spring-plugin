package io.nixer.nixerplugin.stigma.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;

/**
 * Created on 2019-06-12.
 *
 * @author gcwiak
 */
public interface StigmaStorage {

    void saveStigma(@Nonnull final StigmaData stigmaData);

    @Nullable
    StigmaData findStigmaData(@Nonnull final Stigma stigma);

    void updateStatus(@Nonnull final Stigma stigma, final StigmaStatus status);

    // TODO historical
    default void recordSpottingUnknownStigma(@Nonnull final Stigma stigma) {
    }

    // TODO historical
    default void recordStigmaObservation(@Nonnull final StigmaData stigmaData) {
    }
}

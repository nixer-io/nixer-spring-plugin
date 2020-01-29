package io.nixer.nixerplugin.stigma.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;

/**
 * Created on 2019-06-12.
 *
 * @author gcwiak
 */
public interface StigmaStorage {

    void save(@Nonnull final StigmaDetails stigmaDetails);

    @Nullable
    StigmaDetails findStigmaDetails(@Nonnull final Stigma stigma);

    void updateStatus(@Nonnull final Stigma stigma, final StigmaStatus newStatus);

    default void recordSpottingUnknownStigma(@Nonnull final Stigma stigma) {
    }

    default void recordStigmaObservation(@Nonnull final StigmaDetails stigmaDetails) {
    }
}

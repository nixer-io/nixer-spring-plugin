package io.nixer.nixerplugin.core.stigma.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;

/**
 * Created on 2019-06-12.
 *
 * @author gcwiak
 */
public interface StigmaTokenStorage {

    void saveStigma(Stigma stigma, final StigmaStatus status);

    @Nullable
    StigmaData findStigmaData(@Nonnull final Stigma stigma);

    void updateStatus(@Nonnull final Stigma stigma, final StigmaStatus status);

    // TODO historical
    default void recordSpottingUnknownStigma(@Nonnull final Stigma stigma) {
    }

    // TODO historical
    default void recordStigmaObservation(@Nonnull final StigmaData stigmaData) {
    }

    // TODO historical
    default void recordUnreadableToken(@Nonnull final RawStigmaToken token) {
    }
}

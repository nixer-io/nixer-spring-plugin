package io.nixer.nixerplugin.core.stigma.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.evaluate.Stigma;

/**
 * Created on 2019-06-12.
 *
 * @author gcwiak
 */
public interface StigmaTokenStorage {

    @Nonnull
    Stigma createStigma(String stigmaValue, final StigmaStatus status);

    @Nullable
    StigmaData findStigmaData(@Nonnull final Stigma stigma);

    void revokeStigma(@Nonnull final String stigmaValue);

    void recordSpottingUnknownStigma(@Nonnull final Stigma stigma);

    // TODO historical
    default void recordStigmaObservation(@Nonnull final StigmaData stigmaValueData) {
    }

    // TODO historical
    default void recordUnreadableToken(@Nonnull final String rawToken) {
    }
}

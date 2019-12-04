package io.nixer.nixerplugin.core.stigma.storage;

import java.security.SecureRandom;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.evaluate.Stigma;

/**
 * Created on 2019-06-12.
 *
 * @author gcwiak
 */
public interface StigmaTokenStorage {
    // FIXME real implementation to be done!

    // TODO find a better name in order to avoid confusion with eu.xword.nixer.stigma.services.api.StigmaTokenStore

    @Nonnull
    Stigma fetchNewStigma();

    @Nullable
    StigmaData findStigmaValueData(@Nonnull final Stigma stigma);

    void revokeStigma(@Nonnull final StigmaData stigmaValueData);

    void recordStigmaObservation(@Nonnull final StigmaData stigmaValueData);

    void recordUnreadableToken(@Nonnull final String rawToken);

    void recordSpottingUnknownStigma(@Nonnull final Stigma stigma);

    void recordLoginSuccessTokenValid(final Stigma stigma); // TODO do we need this recording?

    void recordLoginSuccessTokenInvalid(final String rawToken, final String tokenValidationResult); // TODO do we need this recording?

    void recordLoginFailTokenInvalid(final String rawToken, final String tokenValidationResult); // TODO do we need this recording?

    class FakeStigmaTokenStorage implements StigmaTokenStorage {

        // FIXME create a real implementation!

        private final SecureRandom stigmaSource = new SecureRandom();

        @Nonnull
        @Override
        public Stigma fetchNewStigma() {
            return new Stigma(String.valueOf(stigmaSource.nextLong()));
        }

        @Nullable
        @Override
        public StigmaData findStigmaValueData(@Nonnull final Stigma stigma) {
            return new StigmaData(
                    UUID.randomUUID(),
                    stigma.getValue(),
                    StigmaStatus.ACTIVE
            );
        }

        @Override
        public void revokeStigma(@Nonnull final StigmaData stigmaValueData) {

        }

        @Override
        public void recordStigmaObservation(@Nonnull final StigmaData stigmaValueData) {

        }

        @Override
        public void recordUnreadableToken(@Nonnull final String rawToken) {

        }

        @Override
        public void recordSpottingUnknownStigma(@Nonnull final Stigma stigma) {

        }

        @Override
        public void recordLoginSuccessTokenValid(final Stigma stigma) {

        }

        @Override
        public void recordLoginSuccessTokenInvalid(final String rawToken, final String tokenValidationResult) {

        }

        @Override
        public void recordLoginFailTokenInvalid(final String rawToken, final String tokenValidationResult) {

        }
    }
}

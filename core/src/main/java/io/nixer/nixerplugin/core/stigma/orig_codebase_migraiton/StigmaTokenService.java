package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import java.security.SecureRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaTokenService implements StigmaTokenStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(StigmaTokenService.class);

    @Nonnull
    private final StigmaTokenProvider stigmaTokenProvider;

    @Nonnull
    private final StigmaTokenStorage stigmaTokenStorage;

    @Nonnull
    private final StigmaExtractor stigmaExtractor;

    // FIXME externalize stigmaSource
    private final SecureRandom stigmaSource = new SecureRandom();

    public StigmaTokenService(@Nonnull final StigmaTokenProvider stigmaTokenProvider,
                              @Nonnull final StigmaTokenStorage stigmaTokenStorage,
                              @Nonnull final StigmaExtractor stigmaExtractor) {
        this.stigmaTokenProvider = Preconditions.checkNotNull(stigmaTokenProvider, "stigmaTokenProvider");
        this.stigmaTokenStorage = Preconditions.checkNotNull(stigmaTokenStorage, "stigmaTokenStorage");
        this.stigmaExtractor = Preconditions.checkNotNull(stigmaExtractor, "stigmaExtractor");
    }

    @Override
    @Nonnull
    public StigmaTokenFetchResult fetchTokenOnLoginSuccess(@Nullable final String originalRawToken) {

        @Nullable final StigmaData stigmaData = stigmaExtractor.tryExtractingStigma(originalRawToken);

        if (isStigmaValid(stigmaData)) {
            // TODO do we need this recording?
            // covered by observed: active->active
            // stigmaTokenStorage.recordLoginSuccessTokenValid(stigmaData.getStigmaValue());

            return new StigmaTokenFetchResult(originalRawToken, true);

        } else {
            // TODO do we need this recording?
            // covered by observed event or recording incoming_unreadable_token
            // stigmaTokenStorage.recordLoginSuccessTokenInvalid(originalRawToken, stigmaValueData.toString());

            return new StigmaTokenFetchResult(newToken(), false);
        }
    }

    private boolean isStigmaValid(@Nullable final StigmaData stigmaData) {
        return stigmaData != null
                && stigmaData.getStatus() == StigmaStatus.ACTIVE;
    }

    @Override
    @Nonnull
    public StigmaTokenFetchResult fetchTokenOnLoginFail(@Nullable final String originalRawToken) {

        @Nullable final StigmaData stigmaData = stigmaExtractor.tryExtractingStigma(originalRawToken);

        if (isStigmaValid(stigmaData)) {
            revokeStigma(stigmaData);

            return new StigmaTokenFetchResult(newToken(), true);

        } else {
            // TODO do we need this recording?
            // covered by observe event or recording incoming_unreadable_token
            // stigmaTokenStorage.recordLoginFailTokenInvalid(originalRawToken, stigmaTokenCheckResult.toString());

            return new StigmaTokenFetchResult(newToken(), false);
        }
    }

    private void revokeStigma(final StigmaData stigmaValueData) {
        try {
            stigmaTokenStorage.revokeStigma(stigmaValueData);
        } catch (Exception e) {
            LOGGER.error("Could not revoke stigma for stigma value data: '{}'", stigmaValueData, e);
        }
    }

    @Nonnull
    private String newToken() {

        // FIXME persist the new stigma into a storage
        final String stigmaValue = String.valueOf(stigmaSource.nextLong());//stigmaTokenStorage.fetchNewStigma();

        return stigmaTokenProvider.getToken(stigmaValue).serialize();
    }
}

package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * External API facing client applications.
 *
 * Created on 2019-06-13.
 *
 * @author gcwiak
 */
public interface StigmaTokenStore {

    /**
     * To be called after successful login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a new token for further usage
     * with information about validity of the original token.
     */
    @Nonnull
    StigmaTokenFetchResult fetchTokenOnLoginSuccess(@Nullable String originalRawToken);

    /**
     * To be called after failed login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a new token for further usage
     * with information about validity of the original token.
     */
    @Nonnull
    StigmaTokenFetchResult fetchTokenOnLoginFail(@Nullable String originalRawToken);

}

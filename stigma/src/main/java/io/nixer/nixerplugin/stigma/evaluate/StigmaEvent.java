package io.nixer.nixerplugin.stigma.evaluate;

/**
 * Represents a login attempt and it's result in combination with status of Stigma Token used during this attempt.
 *
 * Created on 2019-05-13.
 *
 * @author gcwiak
 */
public enum StigmaEvent {

    TOKEN_GOOD_LOGIN_SUCCESS(false),

    TOKEN_GOOD_LOGIN_FAIL(true),
    TOKEN_BAD_LOGIN_SUCCESS(true),
    TOKEN_BAD_LOGIN_FAIL(true);

    public final boolean requiresStigmaRefresh;

    StigmaEvent(final boolean requiresStigmaRefresh) {
        this.requiresStigmaRefresh = requiresStigmaRefresh;
    }
}

package io.nixer.nixerplugin.core.login;

/**
 * Abstraction for storing login result
 */
public interface LoginActivityRepository {

    void save(final LoginResult result, final LoginContext context);
}

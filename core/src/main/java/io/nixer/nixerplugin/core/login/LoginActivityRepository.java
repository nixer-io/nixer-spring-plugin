package io.nixer.nixerplugin.core.login;

/**
 * Abstraction for storing login result
 */
public interface LoginActivityRepository {

    void save(final LoginContext context);
}

package eu.xword.nixer.nixerplugin.login;

/**
 * Abstraction for storing login result
 */
public interface LoginActivityRepository {

    void save(final LoginResult result, final LoginContext context);
}

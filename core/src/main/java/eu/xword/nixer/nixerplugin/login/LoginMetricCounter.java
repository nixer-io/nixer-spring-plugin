package eu.xword.nixer.nixerplugin.login;

/**
 * Abstraction for
 */
public interface LoginMetricCounter {

    void onLogin(final LoginResult result, final LoginContext context);
}

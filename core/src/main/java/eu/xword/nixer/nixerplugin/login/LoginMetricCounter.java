package eu.xword.nixer.nixerplugin.login;

/**
 * Abstraction for counter that updates it value to login result.
 */
public interface LoginMetricCounter {

    void onLogin(final LoginResult result, final LoginContext context);
}
